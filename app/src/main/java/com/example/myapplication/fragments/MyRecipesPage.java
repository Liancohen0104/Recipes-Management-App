package com.example.myapplication.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapters.RecipesAdapter;
import com.example.myapplication.interfaces.OnClickListener;
import com.example.myapplication.models.Recipe;
import com.example.myapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyRecipesPage extends Fragment implements OnClickListener {

    private RecyclerView recyclerView;
    private RecipesAdapter adapter;
    private ArrayList<Recipe> favoriteRecipes = new ArrayList<>();

    public MyRecipesPage() {
    }

    public static MyRecipesPage newInstance(String param1, String param2) {
        MyRecipesPage fragment = new MyRecipesPage();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorites_page, container, false);

        recyclerView = view.findViewById(R.id.favoritesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecipesAdapter(requireContext(), favoriteRecipes, this);
        recyclerView.setAdapter(adapter);

        loadUserFavorites();

        return view;
    }

    private void loadUserFavorites() {
        String currentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.orderByChild("email").equalTo(currentEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String phone = userSnapshot.getKey();

                        DatabaseReference recipesRef = database.getReference("users").child(phone).child("Favorite recipes");

                        recipesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot recipeSnapshot) {
                                favoriteRecipes.clear();

                                for (DataSnapshot product : recipeSnapshot.getChildren()) {
                                    String name = product.child("name").getValue(String.class);
                                    String image = product.child("image").getValue(String.class);
                                    String instructions = product.child("instructions").getValue(String.class);

                                    // שליפת המצרכים
                                    ArrayList<String> ingredientsList = new ArrayList<>();
                                    for (DataSnapshot ingredientSnapshot : product.child("ingredients").getChildren()) {
                                        ingredientsList.add(ingredientSnapshot.getValue(String.class));
                                    }

                                    // שליפת הכמויות
                                    ArrayList<String> measuresList = new ArrayList<>();
                                    for (DataSnapshot measureSnapshot : product.child("quantities").getChildren()) {
                                        measuresList.add(measureSnapshot.getValue(String.class));
                                    }

                                    Recipe recipeModel = new Recipe(name, image, ingredientsList, measuresList, instructions);
                                    recipeModel.setFavorite(true);
                                    favoriteRecipes.add(recipeModel);
                                }

                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Toast.makeText(getContext(), "Failed to load products: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeRecipeFromFavorites(String recipeName) {
        String currentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.orderByChild("email").equalTo(currentEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    String phone = user.getPhone();

                    // הפניה לנתיב של המתכונים
                    DatabaseReference productsRef = database.getReference("users").child(phone).child("Favorite recipes");

                    productsRef.child(recipeName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot productSnapshot) {
                            if (productSnapshot.exists()) {
                                // המתכון קיים - מחיקה
                                productsRef.child(recipeName).removeValue()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                favoriteRecipes.removeIf(recipe -> recipe.getName().equals(recipeName));
                                                adapter.notifyDataSetChanged();
                                                Toast.makeText(getContext(), "Recipe removed successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "Removing the recipe failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                // המתכון לא קיים
                                Toast.makeText(getContext(), "This recipe is not in favorites", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Error accessing recipe data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error accessing user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void OnFavoriteClicked(Recipe recipe) {
        if (recipe.isFavorite()) {
            removeRecipeFromFavorites(recipe.getName());
        }

        // עדכון מצב המועדף במתכון
        recipe.setFavorite(!recipe.isFavorite());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void OnRecipeClicked(Recipe recipe) {
        Bundle bundle = new Bundle();
        bundle.putString("recipeName", recipe.getName());
        bundle.putString("recipeImage", recipe.getImage());
        bundle.putStringArrayList("recipeIngredients", recipe.getIngredients());
        bundle.putStringArrayList("recipeQuantities", recipe.getMeasures());
        bundle.putString("recipeInstructions", recipe.getInstructions());

        Navigation.findNavController(getView()).navigate(R.id.action_Favorites_to_RecipeDetails, bundle);
    }
}
