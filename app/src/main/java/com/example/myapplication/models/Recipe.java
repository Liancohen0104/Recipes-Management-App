package com.example.myapplication.models;

import java.util.ArrayList;

public class Recipe {
    private String name; // שם המתכון
    private String image; // כתובת התמונה
    private ArrayList<String> ingredients; // רשימת מצרכים
    private ArrayList<String> measures; // רשימת כמויות
    private String instructions; // הוראות הכנה
    private boolean isFavorite; // האם במועדפים

    public Recipe(){
    }

    public Recipe(String name, String image, ArrayList<String> ingredients, ArrayList<String> measures, String instructions) {
        this.name = name;
        this.image = image;
        this.ingredients = ingredients;
        this.measures = measures;
        this.instructions = instructions;
        isFavorite = false;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public ArrayList<String> getMeasures() {
        return measures;
    }

    public String getInstructions() {
        return instructions;
    }
}
