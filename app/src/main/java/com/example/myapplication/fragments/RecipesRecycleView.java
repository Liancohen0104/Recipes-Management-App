package com.example.myapplication.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.adapters.RecipesAdapter;
import com.example.myapplication.interfaces.OnClickListener;
import com.example.myapplication.models.User;
import com.example.myapplication.services.DataService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.R;
import com.example.myapplication.models.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecipesRecycleView extends Fragment implements OnClickListener
{
    private ArrayList<Recipe> recipeList;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecipesAdapter adapter;
    TextView nameTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipes_recycle_view, container, false);

        // כפתור למעבר למועדפים
        Button favoritesButton = view.findViewById(R.id.showFavoritesButton);
        favoritesButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_Recipes_to_Favorites));

        recipeList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.favoritesRecyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecipesAdapter(requireContext(), recipeList, this);
        recyclerView.setAdapter(adapter);

        // הצגת שם המשתמש המחובר
        FirebaseAuth auth = FirebaseAuth.getInstance();
        nameTextView = view.findViewById(R.id.userNameTextView);
        if (auth.getCurrentUser() != null) {
            String email = auth.getCurrentUser().getEmail();
            nameTextView.setText("Hello, " + email);
        } else {
            nameTextView.setText("No user logged in");
        }

        // חיפוש
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                get10SearchNameRecipes(newText);
                return true;
            }
        });

        // Spinner
        Spinner categorySpinner = view.findViewById(R.id.spinner);

        // רשימת קטגוריות
        ArrayList<String> categories = new ArrayList<>();
        categories.add("All"); // קטגוריית ברירת מחדל
        categories.add("Breakfast");
        categories.add("Beef");
        categories.add("Chicken");
        categories.add("Pasta");
        categories.add("Vegan");
        categories.add("Vegetarian");
        categories.add("Dessert");

        // הגדרת Adapter ל-Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        // האזנה לבחירת קטגוריה
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories.get(position);

                if (selectedCategory.equals("All")) {
                    get10RandomRecipes(); // חיפוש כללי
                } else {
                    get10CategoryRecipes(selectedCategory); // חיפוש לפי קטגוריה
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // ברירת מחדל
                get10RandomRecipes();
            }
        });

        // שליפת מתכונים
        get10RandomRecipes();
        return view;
    }

    private void get10RandomRecipes()
    {
        recipeList.clear();
        recipeList.addAll(DataService.getRandomRecipes());
        adapter.notifyDataSetChanged();
    }

    private void get10SearchNameRecipes(String recipeName)
    {
        recipeList.clear();
        recipeList.addAll(DataService.getRecipesByName(recipeName));
        adapter.notifyDataSetChanged();
    }

    private void get10CategoryRecipes(String category)
    {
        recipeList.clear();
        recipeList.addAll(DataService.getRecipesByCategory(category));
        adapter.notifyDataSetChanged();
    }

    // שליפת מתכונים מחדש בכל פעם שחוזרים ל-Fragment
    @Override
    public void onResume() {
        super.onResume();
        get10RandomRecipes();
    }

    private void AddFavoriteToDB(Recipe recipe)
    {
        String currentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.orderByChild("email").equalTo(currentEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    String phone = user.getPhone();

                    // יצירת אובייקט מותאם אישית
                    Map<String, Object> productData = new HashMap<>();
                    productData.put("name", recipe.getName());
                    productData.put("image", recipe.getImage());
                    productData.put("ingredients", recipe.getIngredients());
                    productData.put("quantities", recipe.getMeasures());
                    productData.put("instructions", recipe.getInstructions());


                    DatabaseReference productsRef = database.getReference("users").child(phone).child("Favorite recipes");
                    productsRef.child(recipe.getName()).setValue(productData)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Recipe added successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Adding the recipe failed", Toast.LENGTH_SHORT).show();
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

    private void RemoveFavoriteFromDB(String recipeName)
    {
        String currentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.orderByChild("email").equalTo(currentEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    String phone = user.getPhone();

                    // הפניה לנתיב של המוצרים
                    DatabaseReference productsRef = database.getReference("users").child(phone).child("Favorite recipes");

                    productsRef.child(recipeName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot productSnapshot) {
                            if (productSnapshot.exists()) {
                                // המוצר קיים - מחיקה
                                productsRef.child(recipeName).removeValue()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Recipe removed successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "Removing the recipe failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                // המוצר לא קיים
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
    public void OnFavoriteClicked(Recipe recipe)
    {
        if (!recipe.isFavorite())
        {
            AddFavoriteToDB(recipe);
        }
        else
        {
            RemoveFavoriteFromDB(recipe.getName());
        }

        // עדכון מצב המועדף במתכון
        recipe.setFavorite(!recipe.isFavorite());

        // עדכון תמונת הכוכב בתצוגה
        recyclerView.post(() -> adapter.notifyItemChanged(recipeList.indexOf(recipe)));

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

        Navigation.findNavController(getView()).navigate(R.id.action_Recipes_to_RecipeDetails, bundle);
    }
}