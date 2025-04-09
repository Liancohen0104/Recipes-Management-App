package com.example.myapplication.services;

import android.os.StrictMode;
import com.example.myapplication.models.Recipe;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DataService
{
    public static ArrayList<Recipe> getRandomRecipes() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        String baseUrl = "https://www.themealdb.com/api/json/v1/1/random.php";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            for (int i = 0; i < 10; i++) {
                URL url = new URL(baseUrl);
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.connect();

                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
                JsonObject rootObject = root.getAsJsonObject();
                JsonArray mealsArray = rootObject.getAsJsonArray("meals");

                if (mealsArray != null && mealsArray.size() > 0) {
                    JsonObject mealObject = mealsArray.get(0).getAsJsonObject();

                    String name = mealObject.has("strMeal") && !mealObject.get("strMeal").isJsonNull() ? mealObject.get("strMeal").getAsString() : "Unknown";
                    String image = mealObject.has("strMealThumb") && !mealObject.get("strMealThumb").isJsonNull() ? mealObject.get("strMealThumb").getAsString() : "";
                    String instructions = mealObject.has("strInstructions") && !mealObject.get("strInstructions").isJsonNull() ? mealObject.get("strInstructions").getAsString() : "No instructions available";
                    String youtubeUrl = mealObject.has("strYoutube") && !mealObject.get("strYoutube").isJsonNull() ? mealObject.get("strYoutube").getAsString() : "";

                    ArrayList<String> ingredients = new ArrayList<>();
                    ArrayList<String> measures = new ArrayList<>();
                    for (int j = 1; j <= 20; j++) {
                        String ingredientKey = "strIngredient" + j;
                        String measureKey = "strMeasure" + j;

                        if (mealObject.has(ingredientKey) && !mealObject.get(ingredientKey).isJsonNull()) {
                            String ingredient = mealObject.get(ingredientKey).getAsString().trim();
                            if (!ingredient.isEmpty()) ingredients.add(ingredient);
                        }

                        if (mealObject.has(measureKey) && !mealObject.get(measureKey).isJsonNull()) {
                            String measure = mealObject.get(measureKey).getAsString().trim();
                            if (!measure.isEmpty()) measures.add(measure);
                        }
                    }

                    recipes.add(new Recipe(name, image, ingredients, measures, instructions, youtubeUrl));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return recipes;
    }

    public static ArrayList<Recipe> getRecipesByName(String recipeName) {
        ArrayList<Recipe> recipes = new ArrayList<>();
        String baseUrl = "https://www.themealdb.com/api/json/v1/1/search.php?s=" + recipeName;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL(baseUrl);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject rootObject = root.getAsJsonObject();
            JsonArray mealsArray = rootObject.getAsJsonArray("meals");

            if (mealsArray != null && mealsArray.size() > 0) {
                for (int i = 0; i < mealsArray.size() && i < 10; i++) {
                    JsonObject mealObject = mealsArray.get(i).getAsJsonObject();

                    String name = mealObject.has("strMeal") && !mealObject.get("strMeal").isJsonNull() ? mealObject.get("strMeal").getAsString() : "Unknown";
                    String image = mealObject.has("strMealThumb") && !mealObject.get("strMealThumb").isJsonNull() ? mealObject.get("strMealThumb").getAsString() : "";
                    String instructions = mealObject.has("strInstructions") && !mealObject.get("strInstructions").isJsonNull() ? mealObject.get("strInstructions").getAsString() : "No instructions available";
                    String youtubeUrl = mealObject.has("strYoutube") && !mealObject.get("strYoutube").isJsonNull() ? mealObject.get("strYoutube").getAsString() : "";

                    ArrayList<String> ingredients = new ArrayList<>();
                    ArrayList<String> measures = new ArrayList<>();
                    for (int j = 1; j <= 20; j++) {
                        String ingredientKey = "strIngredient" + j;
                        String measureKey = "strMeasure" + j;

                        if (mealObject.has(ingredientKey) && !mealObject.get(ingredientKey).isJsonNull()) {
                            String ingredient = mealObject.get(ingredientKey).getAsString().trim();
                            if (!ingredient.isEmpty()) ingredients.add(ingredient);
                        }

                        if (mealObject.has(measureKey) && !mealObject.get(measureKey).isJsonNull()) {
                            String measure = mealObject.get(measureKey).getAsString().trim();
                            if (!measure.isEmpty()) measures.add(measure);
                        }
                    }

                    recipes.add(new Recipe(name, image, ingredients, measures, instructions, youtubeUrl));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return recipes;
    }

    public static ArrayList<Recipe> getRecipesByCategory(String category) {
        ArrayList<Recipe> recipes = new ArrayList<>();
        String baseUrl = "https://www.themealdb.com/api/json/v1/1/filter.php?c=" + category;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL(baseUrl);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject rootObject = root.getAsJsonObject();
            JsonArray mealsArray = rootObject.getAsJsonArray("meals");

            if (mealsArray != null && mealsArray.size() > 0) {
                for (int i = 0; i < mealsArray.size(); i++) {
                    JsonObject mealObject = mealsArray.get(i).getAsJsonObject();

                    String idMeal = mealObject.has("idMeal") && !mealObject.get("idMeal").isJsonNull() ? mealObject.get("idMeal").getAsString() : "";

                    if (!idMeal.isEmpty()) {
                        String detailsUrl = "https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + idMeal;
                        URL detailsRequestUrl = new URL(detailsUrl);
                        HttpURLConnection detailsRequest = (HttpURLConnection) detailsRequestUrl.openConnection();
                        detailsRequest.connect();

                        JsonElement detailsRoot = jp.parse(new InputStreamReader((InputStream) detailsRequest.getContent()));
                        JsonObject detailsRootObject = detailsRoot.getAsJsonObject();
                        JsonArray detailsMealsArray = detailsRootObject.getAsJsonArray("meals");

                        if (detailsMealsArray != null && detailsMealsArray.size() > 0) {
                            JsonObject detailsMealObject = detailsMealsArray.get(0).getAsJsonObject();

                            String name = detailsMealObject.has("strMeal") && !detailsMealObject.get("strMeal").isJsonNull() ? detailsMealObject.get("strMeal").getAsString() : "Unknown";
                            String image = detailsMealObject.has("strMealThumb") && !detailsMealObject.get("strMealThumb").isJsonNull() ? detailsMealObject.get("strMealThumb").getAsString() : "";
                            String instructions = detailsMealObject.has("strInstructions") && !detailsMealObject.get("strInstructions").isJsonNull() ? detailsMealObject.get("strInstructions").getAsString() : "No instructions available";
                            String youtubeUrl = detailsMealObject.has("strYoutube") && !detailsMealObject.get("strYoutube").isJsonNull() ? detailsMealObject.get("strYoutube").getAsString() : "";

                            ArrayList<String> ingredients = new ArrayList<>();
                            ArrayList<String> measures = new ArrayList<>();
                            for (int j = 1; j <= 20; j++) {
                                String ingredientKey = "strIngredient" + j;
                                String measureKey = "strMeasure" + j;

                                if (detailsMealObject.has(ingredientKey) && !detailsMealObject.get(ingredientKey).isJsonNull()) {
                                    String ingredient = detailsMealObject.get(ingredientKey).getAsString().trim();
                                    if (!ingredient.isEmpty()) ingredients.add(ingredient);
                                }

                                if (detailsMealObject.has(measureKey) && !detailsMealObject.get(measureKey).isJsonNull()) {
                                    String measure = detailsMealObject.get(measureKey).getAsString().trim();
                                    if (!measure.isEmpty()) measures.add(measure);
                                }
                            }

                            recipes.add(new Recipe(name, image, ingredients, measures, instructions, youtubeUrl));
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return recipes;
    }
}
