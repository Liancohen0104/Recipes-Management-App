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
    private List<Recipe> recipes;
    private OnClickListener listener;
    private Context context;

    public RecipesAdapter(Context context, List<Recipe> recipes, OnClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_row, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        // עדכון פרטי המתכון
        holder.recipeNameTextView.setText(recipe.getName());

        // עדכון תמונת המתכון
        Picasso.get()
                .load(recipe.getImage())
                .into(holder.recipeImageView);

        holder.favoriteButton.setOnClickListener(v -> {
            listener.OnFavoriteClicked(recipe);
        });

        holder.itemView.setOnClickListener(v -> {
            listener.OnRecipeClicked(recipe);
        });

        if (recipe.isFavorite()) {
            holder.favoriteButton.setImageResource(android.R.drawable.btn_star_big_on); // כוכב מלא
        } else {
            holder.favoriteButton.setImageResource(android.R.drawable.btn_star_big_off); // כוכב ריק
        }
    }

    @Override
    public int getItemCount() {
        return recipes.size(); // מחזיר את גודל הרשימה
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView recipeNameTextView;
        public ImageView recipeImageView, favoriteButton;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameTextView = itemView.findViewById(R.id.name);
            recipeImageView = itemView.findViewById(R.id.image);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }
    }
}
