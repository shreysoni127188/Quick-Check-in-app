package com.example.self_check_in_app;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.auth.FirebaseAuth;


/**
 * Here we will allow user to reset there password by sending reset link from firebase.
 */
public class ForgetPassword extends Fragment {


    private FirebaseAuth firebaseAuth;
    private Button sendlink;
    private TextInputLayout Email_id_inputLayout;
    private TextInputEditText emailid;
    private TextView sendlinksucess;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private Network network;
    private NavController navController;
    public ForgetPassword() {
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
         network =  new Network(getContext());
        return inflater.inflate(R.layout.fragment_forget_password, container, false);
    }
    /**
     * Reset link will be send if user email-id is valid and is in record of firebase.
     * In case of no internet connection user won't be able to send the link.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendlink = view.findViewById(R.id.sendlink);
        emailid = view.findViewById(R.id.Email_id_edit_text);
        Email_id_inputLayout = view.findViewById(R.id.Email_id_input_box);
        sendlinksucess = view.findViewById(R.id.LinkSendSucess);
        sendlinksucess.setVisibility(View.INVISIBLE);
        firebaseAuth = firebaseAuth.getInstance();
        navController = Navigation.findNavController(view);

        sendlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!emailid.getText().toString().isEmpty()) {
                    Email_id_inputLayout.setErrorEnabled(false);
                    sendlink.setEnabled(true);
                    if (!emailid.getText().toString().matches(emailPattern)) {
                        Email_id_inputLayout.setError("Invalid Email-id");
                        Email_id_inputLayout.getEditText().addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(emailid.getText().toString().matches(emailPattern)){
                                    Email_id_inputLayout.setError(null);
                                    Email_id_inputLayout.setErrorEnabled(false);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        sendlink.setEnabled(true);
                        return;
                    } else {
                        if (network.isConnectingToInternet()) {
                            sendlink.setEnabled(false);

                            firebaseAuth.sendPasswordResetEmail(emailid.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Email_id_inputLayout.setErrorEnabled(false);
                                        sendlinksucess.setVisibility(View.VISIBLE);
                                        sendlink.setEnabled(true);
                                        navController.navigate(R.id.action_forgetPassword_to_loginscreen);
                                    } else {
                                        try {
                                            throw task.getException();
                                        } catch (Exception e) {
                                            Email_id_inputLayout.setError("Email-Id not found");
                                            sendlink.setEnabled(true);

                                        }
                                    }
                                }
                            });
                        } else {
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
                }else {
                    Email_id_inputLayout.setError("Require Email-id");
                    sendlink.setEnabled(true);
                }


            }



            });


        }
}