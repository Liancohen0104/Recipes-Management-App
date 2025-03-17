package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {
    private List<String> ingredients;
    private List<String> quantities;

    public IngredientsAdapter(List<String> ingredients, List<String> quantities) {
        this.ingredients = ingredients;
        this.quantities = quantities;
    }

    @NonNull
    @Override
    public IngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_card_view, parent, false);
        return new IngredientsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsViewHolder holder, int position) {
        holder.ingredientTextView.setText(ingredients.get(position));
        holder.quantityTextView.setText(quantities.get(position));
    }

    @Override
    public int getItemCount() {
        return Math.min(ingredients.size(), quantities.size()); // ווידוא שלא ניגשים לאינדקסים לא חוקיים
    }

    public static class IngredientsViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientTextView, quantityTextView;

        public IngredientsViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientTextView = itemView.findViewById(R.id.ingredients);
            quantityTextView = itemView.findViewById(R.id.quantities);
        }
    }
}
