package com.example.self_check_in_app;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;

import com.google.firebase.auth.FirebaseUser;

/**
 * Here we will send verification link to user to verify email-id.
 */
public class Emailverify extends Fragment implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private String emailid;
    private TextView emailaddress;
    private Button sendemail;
    private NavController navController;
    private Network network;


    public Emailverify() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        network = new Network(getContext());
        return inflater.inflate(R.layout.fragment_emailverify, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailid = EmailverifyArgs.fromBundle(getArguments()).getEmailId();
        emailaddress = view.findViewById(R.id.Emaild);
        emailaddress.setText(emailid);
        firebaseAuth = FirebaseAuth.getInstance();
        sendemail = view.findViewById(R.id.sendlink);
        navController = Navigation.findNavController(view);
       sendemail.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendlink:
                send_link();
                break;
        }
    }

    /**
     * verification link will be send once user sinup sucessfully.
     */

    private void send_link() {
        if (network.isConnectingToInternet()) {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthEmailException email) {
                            Snackbar.make(getView(),"Something went wrong",BaseTransientBottomBar.LENGTH_SHORT).show();
                        } catch (Exception e) {
                        }
                    } else {
                        Snackbar.make(getView(),"Email verification send", BaseTransientBottomBar.LENGTH_SHORT).setAction("Okay", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
                        navController.navigate(R.id.action_emailverify_to_loginscreen);
                        getActivity().getSupportFragmentManager().popBackStack();

                    }
                }
            });

        }else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("No Internet")
                    .setMessage("Please connect to Internet first.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}