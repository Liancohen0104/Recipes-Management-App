package com.example.myapplication.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.activities.MainActivity;
import com.example.myapplication.adapters.IngredientsAdapter;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class RecipeDetails extends Fragment {

    private TextView recipeName;
    private ImageView recipeImage;
    private TextView recipeInstructions;
    private RecyclerView ingredientsRecyclerView;
    private IngredientsAdapter ingredientsAdapter;
    private ArrayList<String> ingredients;
    private ArrayList<String> quantities;
    ImageView recipeYoutube;

    public RecipeDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_details, container, false);

        recipeName = view.findViewById(R.id.name);
        recipeImage = view.findViewById(R.id.image);
        recipeInstructions = view.findViewById(R.id.instructions);
        ingredientsRecyclerView = view.findViewById(R.id.ingredientsRecyclerView);
        recipeYoutube = view.findViewById(R.id.youtube);

        if (getArguments() != null) {
            String recipeNameText = getArguments().getString("recipeName", "No Name");
            String recipeImageString = getArguments().getString("recipeImage", "");
            ingredients = getArguments().getStringArrayList("recipeIngredients");
            quantities = getArguments().getStringArrayList("recipeQuantities");
            String instructions = getArguments().getString("recipeInstructions", "No Instructions Available");
            String youtube = getArguments().getString("recipeYoutube", "No Youtube Video Available");
            recipeName.setText(recipeNameText);

            if (recipeImageString != null && !recipeImageString.isEmpty()) {
                Picasso.get()
                        .load(recipeImageString)
                        .into(recipeImage);
            } else {
                // טען תמונה חלופית
                Picasso.get()
                        .load(R.drawable.default_image)
                        .into(recipeImage);
            }

            recipeInstructions.setText(instructions);

            recipeYoutube.setOnClickListener(v -> {
                animateClick(v);
                if (youtube != null && !youtube.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtube));
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getContext(),"There is no youtube video",Toast.LENGTH_LONG).show();
                }
            });

            if (ingredients == null) {
                ingredients = new ArrayList<>();
            }
            if (quantities == null) {
                quantities = new ArrayList<>();
            }

            // ווידוא שהשני המערכים זהים בגודל כדי למנוע גישה לא חוקית לאינדקסים
            if (ingredients.size() != quantities.size()) {
                int minSize = Math.min(ingredients.size(), quantities.size());
                ingredients = new ArrayList<>(ingredients.subList(0, minSize));
                quantities = new ArrayList<>(quantities.subList(0, minSize));
            }

            // הגדרת ה-RecyclerView למצרכים
            ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            ingredientsAdapter = new IngredientsAdapter(ingredients, quantities);
            ingredientsRecyclerView.setAdapter(ingredientsAdapter);
        }

        return view;
    }

    private void animateClick(View view) {
        view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100)
                .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(100));
    }
}
