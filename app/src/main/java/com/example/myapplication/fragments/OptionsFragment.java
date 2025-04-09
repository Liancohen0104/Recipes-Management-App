package com.example.myapplication.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class OptionsFragment extends Fragment {

    public OptionsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.options_page, container, false);

        // הצגת שם המשתמש המחובר
        FirebaseAuth auth = FirebaseAuth.getInstance();
        TextView nameTextView = view.findViewById(R.id.userNameTextView);
        if (auth.getCurrentUser() != null) {
            String email = auth.getCurrentUser().getEmail();
            nameTextView.setText("hello, " + email);
        } else {
            nameTextView.setText("No user logged in");
        }

        // כפתור למעבר למועדפים
        Button favoritesButton = view.findViewById(R.id.showFavoritesButton);
        favoritesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                animateClick(v);
                Navigation.findNavController(view).navigate(R.id.action_optionsFragment_to_Favorites);
            }
        });

        // כפתור למעבר למתכונים מהאינטרנט
        Button recipesButton = view.findViewById(R.id.btnInternetRecipes);
        recipesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                animateClick(v);
                Navigation.findNavController(view).navigate(R.id.action_optionsFragment_to_Recipes);
            }
        });

        // כפתור למעבר למתכונים שלי
        Button myRecipesButton = view.findViewById(R.id.btnMyRecipes);
        myRecipesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                animateClick(v);
                Navigation.findNavController(view).navigate(R.id.action_optionsFragment_to_myRecipes);
            }
        });

        return view;
    }

    private void animateClick(View view) {
        view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100)
                .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(100));
    }
}

