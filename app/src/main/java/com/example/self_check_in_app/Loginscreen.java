package com.example.self_check_in_app;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

/**
 * Here user needs to provide email-id and password to log into the app.
 */
public class Loginscreen extends Fragment implements View.OnClickListener {

    private TextInputEditText email,password;
    private TextInputLayout email_layout,password_layput;
    private Button login,signup;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentuser;
    private NavController navController;
    private Network network;
    private ProgressBar progressBar;

    public Loginscreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        firebaseAuth =  FirebaseAuth.getInstance();
        network = new Network(getContext());
        return inflater.inflate(R.layout.fragment_loginscreen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InputMethodManager inputMethod = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethod.hideSoftInputFromWindow(getView().getWindowToken(),0);
        navController = Navigation.findNavController(view);
        currentuser = firebaseAuth.getCurrentUser();
         email = view.findViewById(R.id.Email_edit_text);
        password = view.findViewById(R.id.Password_edit_text);
        email_layout = view.findViewById(R.id.Email_input_box);
        password_layput = view.findViewById(R.id.Password_input_box);
        progressBar = view.findViewById(R.id.Login_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        email_layout.setErrorEnabled(false);
        password_layput.setErrorEnabled(false);
        login = view.findViewById(R.id.Login_Btn);
        signup = view.findViewById(R.id.SignUP_Btn);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        view.findViewById(R.id.Reset_Btn).setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Login_Btn:
                progressBar.setVisibility(View.VISIBLE);
                getLogged_In();
                break;
            case R.id.SignUP_Btn:
                getRegister();
                break;
            case R.id.Reset_Btn:
                getForgetPassword();
                break;
        }
    }

    private void getForgetPassword() {
        navController.navigate(R.id.action_loginscreen_to_forgetPassword);

    }
    /**
     * Password must be mininum 6 character long and email-id should be properly formated.
     *
     */
    private void getLogged_In() {

        final String Useremail = email.getText().toString();
        String Userpassword = password.getText().toString();
        if (network.isConnectingToInternet()) {
            login.setEnabled(false);
            if (!Useremail.isEmpty() && !Userpassword.isEmpty()) {
                if(Userpassword.length()>=6 ) {
                    login.setEnabled(false);
                    email_layout.setErrorEnabled(false);
                    password_layput.setErrorEnabled(false);


                    firebaseAuth.signInWithEmailAndPassword(Useremail, Userpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    InputMethodManager inputMethod = (InputMethodManager)getView().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethod.hideSoftInputFromWindow(getView().getWindowToken(),0);
                                    navController.navigate(R.id.action_loginscreen_to_mainpage);
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    login.setEnabled(true);
                                    LoginscreenDirections.ActionLoginscreenToEmailverify loginscreenToEmailverify = LoginscreenDirections.actionLoginscreenToEmailverify();
                                    loginscreenToEmailverify.setEmailId(Useremail);
                                    navController.navigate(loginscreenToEmailverify);

                                }

                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                login.setEnabled(true);
                                switch (((FirebaseAuthException) task.getException()).getErrorCode()) {

                                    case "ERROR_INVALID_EMAIL":
                                        email_layout.setError("The email address is badly formatted.");
                                        email_layout.requestFocus();
                                        break;

                                    case "ERROR_WRONG_PASSWORD":
                                        password_layput.setError("Password is incorrect ");
                                        break;

                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        email_layout.setError("The email address is already in use by another account.");
                                        email_layout.requestFocus();
                                        break;

                                    case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                        break;

                                    case "ERROR_USER_DISABLED":
                                        break;


                                    case "ERROR_USER_NOT_FOUND":
                                        email_layout.setError("Email - id not registered");
                                        break;




                                }
                                login.setEnabled(true);
                            }
                        }
                    });
                }else {
                    if (password.length() < 6 ) {
                        progressBar.setVisibility(View.INVISIBLE);
                        login.setEnabled(true);
                        password_layput.setError("Invalid Password");
                        password_layput.getEditText().addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                if (s.length() < 1) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    password_layput.setError("Require Password");
                                    password_layput.setErrorEnabled(true);

                                }
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if (s.length() > 6 ) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    password_layput.setError(null);
                                    password_layput.setErrorEnabled(false);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                    }

                }
            } else {
                if (Useremail.isEmpty()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    login.setEnabled(true);
                    email_layout.setError("Require Email Id");
                    email_layout.getEditText().addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            if (s.length() < 1) {
                                email_layout.setError("Require Email Id");
                                email_layout.setErrorEnabled(true);

                            }
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.length() < 1) {
                                email_layout.setError("Require Email Id");
                                email_layout.setErrorEnabled(true);

                            }
                            if (s.length() > 1) {
                                email_layout.setError(null);
                                email_layout.setErrorEnabled(false);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                }
                if (Userpassword.isEmpty()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    login.setEnabled(true);
                    password_layput.setError("Require Password");
                    password_layput.getEditText().addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            if (s.length() < 1) {
                                password_layput.setError("Require Password");
                                password_layput.setErrorEnabled(true);
                            }
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.length() < 1) {
                                password_layput.setError("Require Password");
                                password_layput.setErrorEnabled(true);
                            }
                            if (s.length() > 1) {
                                password_layput.setError(null);
                                password_layput.setErrorEnabled(false);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                }
            }
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
    private void getRegister(){
        ClearLoginCredentials();
        signup.setEnabled(false);
        navController.navigate(R.id.action_loginscreen_to_signUpscreen);
    }
    private void ClearLoginCredentials(){
        email.setText("");
        password.setText("");
        email_layout.setErrorEnabled(false);
        password_layput.setErrorEnabled(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentuser = firebaseAuth.getCurrentUser();
        if(currentuser!=null){
            if(currentuser.isEmailVerified()){
                navController.navigate(R.id.action_loginscreen_to_mainpage);
            }
        }
    }
}