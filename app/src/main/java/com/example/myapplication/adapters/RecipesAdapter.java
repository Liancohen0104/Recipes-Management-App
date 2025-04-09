package com.example.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.interfaces.OnClickListener;
import com.example.myapplication.models.Recipe;
import com.squareup.picasso.Picasso;
import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {
    private final List<Recipe> recipes;
    private final OnClickListener listener;
    private final Context context;
    private final boolean isPersonalRecipe;

    public RecipesAdapter(Context context, List<Recipe> recipes, OnClickListener listener, boolean isPersonalRecipe) {
        this.recipes = recipes;
        this.listener = listener;
        this.context = context;
        this.isPersonalRecipe = isPersonalRecipe;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isPersonalRecipe ? R.layout.card_my_recipes : R.layout.card_row;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.recipeNameTextView.setText(recipe.getName());

        String imageUrl = recipe.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .into(holder.recipeImageView);
        } else {
            // טען תמונה חלופית אם אין URL
            Picasso.get()
                    .load(R.drawable.default_image)
                    .into(holder.recipeImageView);
        }

        if (isPersonalRecipe) {
            holder.actionButton.setImageResource(android.R.drawable.ic_menu_delete);
            holder.actionButton.setOnClickListener(v -> {
                listener.OnRemoveClicked(recipe);
            });
        } else {
            holder.actionButton.setImageResource(
                    recipe.isFavorite() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off
            );
            holder.actionButton.setOnClickListener(v -> {
                listener.OnFavoriteClicked(recipe);
            });
        }

        holder.itemView.setOnClickListener(v -> {
            listener.OnRecipeClicked(recipe);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView recipeNameTextView;
        public ImageView recipeImageView, actionButton;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameTextView = itemView.findViewById(R.id.name);
            recipeImageView = itemView.findViewById(R.id.image);
            actionButton = itemView.findViewById(R.id.favoriteButton);
        }
    }
}
