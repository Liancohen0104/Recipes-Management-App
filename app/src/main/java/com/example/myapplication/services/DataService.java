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
        ArrayList<Recipe> recipes = new ArrayList<>(); // רשימה שתחזיק את 10 המתכונים
        String baseUrl = "https://www.themealdb.com/api/json/v1/1/random.php"; // כתובת ה-API

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            for (int i = 0; i < 10; i++) { // לולאה לקבלת 10 מתכונים
                URL url;
                try {
                    url = new URL(baseUrl);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }

                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.connect();

                JsonParser jp = new JsonParser(); // אובייקט להמרת הנתונים
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); // המרת הנתונים ל-JsonElement
                JsonObject rootObject = root.getAsJsonObject(); // המרת הנתונים ל-JsonObject
                JsonArray mealsArray = rootObject.getAsJsonArray("meals"); // המרת הנתונים מ-JsonObject ל-JsonArray

                if (mealsArray != null && mealsArray.size() > 0) {
                    JsonObject mealObject = mealsArray.get(0).getAsJsonObject(); // תמיד גש לאינדקס 0

                    // בדיקות לשדות המידע
                    String name = mealObject.has("strMeal") && !mealObject.get("strMeal").isJsonNull() ? mealObject.get("strMeal").getAsString() : "Unknown";
                    String image = mealObject.has("strMealThumb") && !mealObject.get("strMealThumb").isJsonNull() ? mealObject.get("strMealThumb").getAsString() : "";
                    String instructions = mealObject.has("strInstructions") && !mealObject.get("strInstructions").isJsonNull() ? mealObject.get("strInstructions").getAsString() : "No instructions available";

                    // יצירת רשימת מצרכים
                    ArrayList<String> ingredients = new ArrayList<>();
                    ArrayList<String> measures = new ArrayList<>();
                    for (int j = 1; j <= 20; j++) {
                        String ingredientKey = "strIngredient" + j;
                        String measureKey = "strMeasure" + j;

                        if (mealObject.has(ingredientKey) && !mealObject.get(ingredientKey).isJsonNull()) {
                            String ingredient = mealObject.get(ingredientKey).getAsString().trim();
                            if (!ingredient.isEmpty()) {
                                ingredients.add(ingredient);
                            }
                        }

                        if (mealObject.has(measureKey) && !mealObject.get(measureKey).isJsonNull()) {
                            String measure = mealObject.get(measureKey).getAsString().trim();
                            if (!measure.isEmpty()) {
                                measures.add(measure);
                            }
                        }
                    }

                    // הוספת המתכון לרשימה
                    recipes.add(new Recipe(name, image, ingredients, measures, instructions));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return recipes;
    }

    public static ArrayList<Recipe> getRecipesByName(String recipeName) {
        ArrayList<Recipe> recipes = new ArrayList<>(); // רשימה שתחזיק את 10 המתכונים
        String baseUrl = "https://www.themealdb.com/api/json/v1/1/search.php?s=" + recipeName; // כתובת ה-API עם פרמטר החיפוש

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url;
            try {
                url = new URL(baseUrl);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            JsonParser jp = new JsonParser(); // אובייקט להמרת הנתונים
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); // המרת הנתונים ל-JsonElement
            JsonObject rootObject = root.getAsJsonObject(); // המרת הנתונים ל-JsonObject
            JsonArray mealsArray = rootObject.getAsJsonArray("meals"); // המרת הנתונים מ-JsonObject ל-JsonArray

            if (mealsArray != null && mealsArray.size() > 0) {
                for (int i = 0; i < mealsArray.size() && i < 10; i++) { // לולאה ל-10 מתכונים או פחות
                    JsonObject mealObject = mealsArray.get(i).getAsJsonObject();

                    // שליפת נתונים
                    String name = mealObject.has("strMeal") && !mealObject.get("strMeal").isJsonNull()
                            ? mealObject.get("strMeal").getAsString()
                            : "Unknown";
                    String image = mealObject.has("strMealThumb") && !mealObject.get("strMealThumb").isJsonNull()
                            ? mealObject.get("strMealThumb").getAsString()
                            : "";
                    String instructions = mealObject.has("strInstructions") && !mealObject.get("strInstructions").isJsonNull()
                            ? mealObject.get("strInstructions").getAsString()
                            : "No instructions available";

                    // רשימות מצרכים וכמויות
                    ArrayList<String> ingredients = new ArrayList<>();
                    ArrayList<String> measures = new ArrayList<>();
                    for (int j = 1; j <= 20; j++) {
                        String ingredientKey = "strIngredient" + j;
                        String measureKey = "strMeasure" + j;

                        if (mealObject.has(ingredientKey) && !mealObject.get(ingredientKey).isJsonNull()) {
                            String ingredient = mealObject.get(ingredientKey).getAsString().trim();
                            if (!ingredient.isEmpty()) {
                                ingredients.add(ingredient);
                            }
                        }

                        if (mealObject.has(measureKey) && !mealObject.get(measureKey).isJsonNull()) {
                            String measure = mealObject.get(measureKey).getAsString().trim();
                            if (!measure.isEmpty()) {
                                measures.add(measure);
                            }
                        }
                    }

                    // הוספת מתכון לרשימה
                    recipes.add(new Recipe(name, image, ingredients, measures, instructions));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return recipes;
    }

    public static ArrayList<Recipe> getRecipesByCategory(String category) {
        ArrayList<Recipe> recipes = new ArrayList<>(); // רשימה שתחזיק את כל המתכונים בקטגוריה
        String baseUrl = "https://www.themealdb.com/api/json/v1/1/filter.php?c=" + category; // כתובת ה-API לשליפת מתכונים לפי קטגוריה

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL(baseUrl);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            JsonParser jp = new JsonParser(); // אובייקט להמרת הנתונים
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); // המרת הנתונים ל-JsonElement
            JsonObject rootObject = root.getAsJsonObject(); // המרת הנתונים ל-JsonObject
            JsonArray mealsArray = rootObject.getAsJsonArray("meals"); // המרת הנתונים מ-JsonObject ל-JsonArray

            if (mealsArray != null && mealsArray.size() > 0) {
                for (int i = 0; i < mealsArray.size(); i++) {
                    JsonObject mealObject = mealsArray.get(i).getAsJsonObject();

                    // שליפת ID של המתכון כדי לקבל את כל המידע כולל המצרכים והכמויות
                    String idMeal = mealObject.has("idMeal") && !mealObject.get("idMeal").isJsonNull()
                            ? mealObject.get("idMeal").getAsString()
                            : "";

                    if (!idMeal.isEmpty()) {
                        // קריאה נוספת לאותו ID כדי לשלוף את כל המידע (כולל המצרכים והכמויות)
                        String detailsUrl = "https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + idMeal;
                        URL detailsRequestUrl = new URL(detailsUrl);
                        HttpURLConnection detailsRequest = (HttpURLConnection) detailsRequestUrl.openConnection();
                        detailsRequest.connect();

                        JsonElement detailsRoot = jp.parse(new InputStreamReader((InputStream) detailsRequest.getContent()));
                        JsonObject detailsRootObject = detailsRoot.getAsJsonObject();
                        JsonArray detailsMealsArray = detailsRootObject.getAsJsonArray("meals");

                        if (detailsMealsArray != null && detailsMealsArray.size() > 0) {
                            JsonObject detailsMealObject = detailsMealsArray.get(0).getAsJsonObject();

                            // שליפת נתונים כלליים
                            String name = detailsMealObject.has("strMeal") && !detailsMealObject.get("strMeal").isJsonNull()
                                    ? detailsMealObject.get("strMeal").getAsString()
                                    : "Unknown";
                            String image = detailsMealObject.has("strMealThumb") && !detailsMealObject.get("strMealThumb").isJsonNull()
                                    ? detailsMealObject.get("strMealThumb").getAsString()
                                    : "";
                            String instructions = detailsMealObject.has("strInstructions") && !detailsMealObject.get("strInstructions").isJsonNull()
                                    ? detailsMealObject.get("strInstructions").getAsString()
                                    : "No instructions available";

                            // שליפת המצרכים והכמויות
                            ArrayList<String> ingredients = new ArrayList<>();
                            ArrayList<String> measures = new ArrayList<>();
                            for (int j = 1; j <= 20; j++) {
                                String ingredientKey = "strIngredient" + j;
                                String measureKey = "strMeasure" + j;

                                if (detailsMealObject.has(ingredientKey) && !detailsMealObject.get(ingredientKey).isJsonNull()) {
                                    String ingredient = detailsMealObject.get(ingredientKey).getAsString().trim();
                                    if (!ingredient.isEmpty()) {
                                        ingredients.add(ingredient);
                                    }
                                }

                                if (detailsMealObject.has(measureKey) && !detailsMealObject.get(measureKey).isJsonNull()) {
                                    String measure = detailsMealObject.get(measureKey).getAsString().trim();
                                    if (!measure.isEmpty()) {
                                        measures.add(measure);
                                    }
                                }
                            }

                            // הוספת המתכון לרשימה
                            recipes.add(new Recipe(name, image, ingredients, measures, instructions));
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
