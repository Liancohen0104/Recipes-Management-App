package com.example.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.adapters.RecipesAdapter;
import com.example.myapplication.interfaces.OnClickListener;
import com.example.myapplication.models.Recipe;
import com.example.myapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;

public class MyRecipes extends Fragment implements OnClickListener {

    private RecyclerView recyclerView;
    private RecipesAdapter adapter;
    private ArrayList<Recipe> myRecipes = new ArrayList<>();

    public MyRecipes() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_recipes, container, false);

        recyclerView = view.findViewById(R.id.favoritesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecipesAdapter(requireContext(), myRecipes, this, true);
        recyclerView.setAdapter(adapter);

        // כפתור פתיחת מסך הוספת מתכון
        Button uploadBtn = view.findViewById(R.id.uploadRecipeButton);
        uploadBtn.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_MyRecipes_to_UploadRecipe));

        loadUserRecipes();

        return view;
    }

    private void loadUserRecipes() {
        String currentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.orderByChild("email").equalTo(currentEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        String phone = user.getPhone();

                        DatabaseReference recipesRef = database.getReference("users").child(phone).child("My Uploaded Recipes");

                        recipesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot recipeSnapshot) {
                                myRecipes.clear();

                                for (DataSnapshot product : recipeSnapshot.getChildren()) {
                                    String name = product.child("name").getValue(String.class);
                                    String image = product.child("image").getValue(String.class);
                                    String instructions = product.child("instructions").getValue(String.class);
                                    String youtube = product.child("youtube").getValue(String.class);

                                    ArrayList<String> ingredientsList = new ArrayList<>();
                                    for (DataSnapshot ingredientSnapshot : product.child("ingredients").getChildren()) {
                                        ingredientsList.add(ingredientSnapshot.getValue(String.class));
                                    }

                                    ArrayList<String> measuresList = new ArrayList<>();
                                    for (DataSnapshot measureSnapshot : product.child("quantities").getChildren()) {
                                        measuresList.add(measureSnapshot.getValue(String.class));
                                    }

                                    Recipe recipeModel = new Recipe(name, image, ingredientsList, measuresList, instructions, youtube);
                                    recipeModel.setFavorite(false);
                                    myRecipes.add(recipeModel);
                                }

                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Toast.makeText(getContext(), "Failed to load recipes", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void OnRemoveClicked(Recipe recipe) {
        String currentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.orderByChild("email").equalTo(currentEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    String phone = user.getPhone();

                    DatabaseReference recipesRef = database.getReference("users")
                            .child(phone)
                            .child("My Uploaded Recipes");

                    recipesRef.child(recipe.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot recipeSnapshot) {
                            if (recipeSnapshot.exists()) {
                                recipesRef.child(recipe.getName()).removeValue()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                // הסרה גם מהרשימה המקומית
                                                myRecipes.removeIf(r -> r.getName().equals(recipe.getName()));
                                                adapter.notifyDataSetChanged();
                                                Toast.makeText(getContext(), "Recipe removed successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "Failed to remove recipe", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(getContext(), "Recipe not found", Toast.LENGTH_SHORT).show();
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
    public void OnRecipeClicked(Recipe recipe) {
        Bundle bundle = new Bundle();
        bundle.putString("recipeName", recipe.getName());
        bundle.putString("recipeImage", recipe.getImage());
        bundle.putStringArrayList("recipeIngredients", recipe.getIngredients());
        bundle.putStringArrayList("recipeQuantities", recipe.getMeasures());
        bundle.putString("recipeInstructions", recipe.getInstructions());
        bundle.putString("recipeYoutube", recipe.getYoutube());

        Navigation.findNavController(getView()).navigate(R.id.action_MyRecipes_to_RecipeDetails, bundle);
    }

    @Override
    public void OnFavoriteClicked(Recipe recipe) {

    }
}
