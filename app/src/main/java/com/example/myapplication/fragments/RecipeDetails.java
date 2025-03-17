package com.example.myapplication.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.myapplication.R;
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

        if (getArguments() != null) {
            String recipeNameText = getArguments().getString("recipeName", "No Name");
            String recipeImageString = getArguments().getString("recipeImage", "");
            ingredients = getArguments().getStringArrayList("recipeIngredients");
            quantities = getArguments().getStringArrayList("recipeQuantities");
            String instructions = getArguments().getString("recipeInstructions", "No Instructions Available");

            recipeName.setText(recipeNameText);
            Picasso.get().load(recipeImageString).into(recipeImage);
            recipeInstructions.setText(instructions);

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
}
