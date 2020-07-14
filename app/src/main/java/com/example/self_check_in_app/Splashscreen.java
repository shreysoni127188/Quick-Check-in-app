package com.example.self_check_in_app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 *
 */
public class Splashscreen extends Fragment {
    private NavController navController;
    private FirebaseAuth firebaseAuth;

    public Splashscreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_splashscreen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        firebaseAuth = FirebaseAuth.getInstance();





    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentuser= firebaseAuth.getCurrentUser();
        if(currentuser!=null){
            if(currentuser.isEmailVerified()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navController.navigate(R.id.action_splashscreen_to_mainpage);
                    }
                }, 3000);
            }else{
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navController.navigate(R.id.action_splashscreen_to_loginscreen);
                    }
                },3000);
            }

        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    navController.navigate(R.id.action_splashscreen_to_loginscreen);
                }
            },3000);

        }

    }

}


