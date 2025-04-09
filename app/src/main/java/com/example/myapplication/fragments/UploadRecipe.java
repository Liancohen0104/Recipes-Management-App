package com.example.myapplication.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UploadRecipe extends Fragment {

    private EditText nameEditText, imageUrlEditText, instructionsEditText, youtubeUrlEditText;
    private LinearLayout ingredientsContainer;
    private Button addIngredientButton, saveButton;

    public UploadRecipe() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upload_recipe, container, false);

        nameEditText = view.findViewById(R.id.recipeNameEditText);
        imageUrlEditText = view.findViewById(R.id.imageUrlEditText);
        instructionsEditText = view.findViewById(R.id.instructionsEditText);
        youtubeUrlEditText = view.findViewById(R.id.youtubeUrlEditText);
        ingredientsContainer = view.findViewById(R.id.ingredientsContainer);
        addIngredientButton = view.findViewById(R.id.addIngredientButton);
        saveButton = view.findViewById(R.id.saveButton);

        addIngredientButton.setOnClickListener(v -> addIngredientRow(null, null));
        saveButton.setOnClickListener(v -> saveRecipe());

        return view;
    }

    private void addIngredientRow(String ingredient, String quantity) {
        View row = LayoutInflater.from(getContext()).inflate(R.layout.ingredient_row, ingredientsContainer, false);

        EditText ingredientEditText = row.findViewById(R.id.ingredientEditText);
        EditText quantityEditText = row.findViewById(R.id.quantityEditText);

        if (ingredient != null) ingredientEditText.setText(ingredient);
        if (quantity != null) quantityEditText.setText(quantity);

        ingredientsContainer.addView(row);
    }

    private void saveRecipe() {
        String name = nameEditText.getText().toString().trim();
        String imageUrl = imageUrlEditText.getText().toString().trim();
        String instructions = instructionsEditText.getText().toString().trim();
        String youtubeUrl = youtubeUrlEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(instructions)) {
            Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> ingredients = new ArrayList<>();
        ArrayList<String> quantities = new ArrayList<>();

        for (int i = 0; i < ingredientsContainer.getChildCount(); i++) {
            View row = ingredientsContainer.getChildAt(i);
            EditText ingredientEditText = row.findViewById(R.id.ingredientEditText);
            EditText quantityEditText = row.findViewById(R.id.quantityEditText);

            String ing = ingredientEditText.getText().toString().trim();
            String qty = quantityEditText.getText().toString().trim();

            if (!TextUtils.isEmpty(ing) && !TextUtils.isEmpty(qty)) {
                ingredients.add(ing);
                quantities.add(qty);
            }
        }

        String currentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.orderByChild("email").equalTo(currentEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    String phone = user.getPhone();

                    DatabaseReference recipesRef = database.getReference("users").child(phone).child("My Uploaded Recipes");

                    Map<String, Object> recipeData = new HashMap<>();
                    recipeData.put("name", name);
                    recipeData.put("image", imageUrl);
                    recipeData.put("instructions", instructions);
                    recipeData.put("youtube", youtubeUrl);
                    recipeData.put("ingredients", ingredients);
                    recipeData.put("quantities", quantities);

                    recipesRef.child(name).setValue(recipeData)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Recipe saved successfully", Toast.LENGTH_SHORT).show();
                                    Navigation.findNavController(getView()).popBackStack();
                                } else {
                                    Toast.makeText(getContext(), "Failed to save recipe", Toast.LENGTH_SHORT).show();
                                }
                            });
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error accessing user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}