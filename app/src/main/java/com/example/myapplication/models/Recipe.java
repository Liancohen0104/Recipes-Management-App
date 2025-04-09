package com.example.myapplication.models;

import java.util.ArrayList;

public class Recipe {
    private String name; // שם המתכון
    private String image; // כתובת התמונה
    private ArrayList<String> ingredients; // רשימת מצרכים
    private ArrayList<String> measures; // רשימת כמויות
    private String instructions; // הוראות הכנה
    private String youtube; // סרטון הדרכה
    private boolean isFavorite; // האם במועדפים

    public Recipe(){
    }

    public Recipe(String name, String image, ArrayList<String> ingredients, ArrayList<String> measures, String instructions, String youtube) {
        this.name = name;
        this.image = image;
        this.ingredients = ingredients;
        this.measures = measures;
        this.instructions = instructions;
        this.youtube = youtube;
        isFavorite = false;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public String getYoutube() { return this.youtube; }

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
