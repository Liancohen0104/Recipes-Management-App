package com.example.myapplication.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.adapters.IngredientsAdapter;
import com.example.myapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;

public class RecipeDetails extends Fragment {

    private TextView recipeName;
    private ImageView recipeImage;
    private TextView recipeInstructions;
    private RecyclerView ingredientsRecyclerView;
    private IngredientsAdapter ingredientsAdapter;
    private ImageView recipeYoutube;

    public RecipeDetails() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_details, container, false);

        recipeName = view.findViewById(R.id.name);
        recipeImage = view.findViewById(R.id.image);
        recipeInstructions = view.findViewById(R.id.instructions);
        ingredientsRecyclerView = view.findViewById(R.id.ingredientsRecyclerView);
        recipeYoutube = view.findViewById(R.id.youtube);

        Bundle args = getArguments();
        if (args != null) {
            String source = args.getString("recipeSource", "");
            if (source.equals("myRecipes")) {
                loadRecipeFromDatabase(args.getString("recipeName", ""));
            } else {
                displayRecipeFromBundle(args);
            }
        }

        return view;
    }

    private void loadRecipeFromDatabase(String recipeNameKey) {
        String currentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.orderByChild("email").equalTo(currentEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String phone = userSnapshot.child("phone").getValue(String.class);
                            DatabaseReference recipeRef = usersRef.child(phone).child("My Uploaded Recipes").child(recipeNameKey);

                            recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot recipeSnap) {
                                    if (recipeSnap.exists()) {
                                        String imageUri = recipeSnap.child("image").getValue(String.class);
                                        String youtubeUri = recipeSnap.child("youtube").getValue(String.class);
                                        String instructions = recipeSnap.child("instructions").getValue(String.class);
                                        String name = recipeSnap.child("name").getValue(String.class);

                                        ArrayList<String> ingredientsList = new ArrayList<>();
                                        for (DataSnapshot ingredientSnap : recipeSnap.child("ingredients").getChildren()) {
                                            ingredientsList.add(ingredientSnap.getValue(String.class));
                                        }

                                        ArrayList<String> quantitiesList = new ArrayList<>();
                                        for (DataSnapshot quantitySnap : recipeSnap.child("quantities").getChildren()) {
                                            quantitiesList.add(quantitySnap.getValue(String.class));
                                        }

                                        displayRecipeData(name, imageUri, instructions, youtubeUri, ingredientsList, quantitiesList);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getContext(), "Failed to load recipe", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayRecipeFromBundle(Bundle args) {
        String name = args.getString("recipeName", "No Name");
        String image = args.getString("recipeImage", "");
        String instructions = args.getString("recipeInstructions", "No Instructions Available");
        String youtube = args.getString("recipeYoutube", "");
        ArrayList<String> ingredients = args.getStringArrayList("recipeIngredients");
        ArrayList<String> quantities = args.getStringArrayList("recipeQuantities");

        displayRecipeData(name, image, instructions, youtube, ingredients, quantities);
    }

    private void displayRecipeData(String name, String image, String instructions, String youtube, ArrayList<String> ingredients, ArrayList<String> quantities) {
        recipeName.setText(name);

        if (image != null && !image.isEmpty()) {
            Glide.with(requireContext())
                    .load(Uri.parse(image))
                    .into(recipeImage);
        } else {
            Glide.with(requireContext())
                    .load(R.drawable.default_image)
                    .into(recipeImage);
        }

        recipeInstructions.setText(instructions);

        recipeYoutube.setOnClickListener(v -> {
            animateClick(v);

            if (youtube != null && !youtube.isEmpty()) {
                Uri videoUri = Uri.parse(youtube);

                Intent intent;

                if (youtube.contains("youtube.com") || youtube.contains("youtu.be")) {
                    //  קישור ליוטיוב - פתח בדפדפן/יוטיוב
                    intent = new Intent(Intent.ACTION_VIEW, videoUri);
                } else {
                    // 🎥 קובץ MP4 מקומי מהמכשיר
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(videoUri, "video/*");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Cannot open video", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "There is no video", Toast.LENGTH_SHORT).show();
            }
        });

        if (ingredients == null) ingredients = new ArrayList<>();
        if (quantities == null) quantities = new ArrayList<>();

        int minSize = Math.min(ingredients.size(), quantities.size());
        ingredients = new ArrayList<>(ingredients.subList(0, minSize));
        quantities = new ArrayList<>(quantities.subList(0, minSize));

        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientsAdapter = new IngredientsAdapter(ingredients, quantities);
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);
    }

    private void animateClick(View view) {
        view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100)
                .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(100));
    }
}