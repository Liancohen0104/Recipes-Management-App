package com.example.myapplication.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activities.MainActivity;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginPage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView mailTextView;
    TextView passwordTextView;

    public LoginPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginPage newInstance(String param1, String param2) {
        LoginPage fragment = new LoginPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.login_page, container, false);
        mailTextView = view.findViewById(R.id.emailText);
        passwordTextView = view.findViewById(R.id.passwordText);

        Button button1 = view.findViewById(R.id.loginButton);
        button1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                animateClick(v);
                boolean callLogin = true;
                String mailString = mailTextView.getText().toString();
                if (TextUtils.isEmpty(mailString))
                {
                    mailTextView.setError("Empty Mail");
                    callLogin = false;
                }

                String passwordString = passwordTextView.getText().toString();
                if (TextUtils.isEmpty(passwordString))
                {
                    passwordTextView.setError("Empty Password");
                    callLogin = false;
                }

                if(callLogin)
                {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.login(view);
                }
            }
        });

        Button button2 = view.findViewById(R.id.registerButton);
        button2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                animateClick(v);
                Navigation.findNavController(view).navigate(R.id.action_Login_to_Register);
            }
        });

      return view;
    }

    private void animateClick(View view) {
        view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100)
                .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(100));
    }
}

