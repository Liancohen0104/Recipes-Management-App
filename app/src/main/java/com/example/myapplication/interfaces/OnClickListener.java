package com.example.myapplication.interfaces;

import com.example.myapplication.models.Recipe;

public interface OnClickListener
{
    void OnFavoriteClicked(Recipe recipe);
    public void OnRemoveClicked(Recipe recipe);
    void OnRecipeClicked(Recipe recipe);
}
