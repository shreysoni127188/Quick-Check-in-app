package com.example.self_check_in_app;


import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;

import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;

import android.widget.ProgressBar;



import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
/**
 *Here user need to enter all information to signup into the app.
 * After checking all the fields user account will be created.
 */
public class SignUpscreen extends Fragment implements View.OnClickListener {

    private TextInputLayout User_username,User_phone_number,User_emailId,User_password,User_confirmPassword;
    private TextInputEditText username,phonenumber,password,confirmpassword,email_id;
    private Button Register;
    private NavController navController;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db ;
    private ProgressBar signup_progress;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
   private FirebaseUser currentuser;
   private Network network;

    public SignUpscreen() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        network = new Network(getContext());
        return inflater.inflate(R.layout.fragment_sign_upscreen, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        username = view.findViewById(R.id.SignUp_Username_edit_text);
        phonenumber = view.findViewById(R.id.Signup_PhoneNumber_edit_text);
        phonenumber.setMaxEms(10);
        email_id = view.findViewById(R.id.Signup_Email_edit_text);
        password = view.findViewById(R.id.SignUp_Password_edit_text);
        confirmpassword = view.findViewById(R.id.SignUp_ConfirmPassword_edit_text);
        navController = Navigation.findNavController(view);
//        Input box
        User_username = view.findViewById(R.id.Username_input_box);
        User_phone_number = view.findViewById(R.id.Signup_Phonenumber_input_box);
        User_emailId = view.findViewById(R.id.Signup_Email_input_box);
        User_password = view.findViewById(R.id.SignUp_Passoword_input_box);
        User_confirmPassword = view.findViewById(R.id.SignUp_ConfirmPassword_input_box);
        signup_progress = view.findViewById(R.id.Signup_progress);
        signup_progress.setVisibility(View.INVISIBLE);
        Register = view.findViewById(R.id.Register);
        Register.setOnClickListener(this);
    }


    /**
     * Username or full name must be more then 6 character long and max 20character .
     * User password must be min 6 character long .
     * email must contain "@" symbol before domain name.
     * Phone number must be 10 character long.
     * Password and confirm password must be same .
     * if same email-id is created before it will through error.
     */
    @Override
    public void onClick(View v) {
        final String username1 = username.getText().toString();
        final String phonenumber1 = phonenumber.getText().toString();
        final String email1 = email_id.getText().toString();
        final String password1 = password.getText().toString();
        final String confirmpassword1 = Objects.requireNonNull(confirmpassword.getText()).toString();
        if(network.isConnectingToInternet()) {
            if (!username1.isEmpty() && !phonenumber1.isEmpty() && !email1.isEmpty() && !password1.isEmpty() && !confirmpassword1.isEmpty()) {
                Log.d("Signupscreen", "Phone length" + phonenumber1.length());
                if (username1.length() >= 6 && username1.length()<=20) {
                    if (phonenumber1.length() > 9 && phonenumber1.matches(String.valueOf(Patterns.PHONE))) {
                        User_phone_number.setErrorEnabled(false);
                        if (email1.matches(emailPattern)) {
                            if (password1.length() > 6 ) {
                                User_password.setErrorEnabled(false);
                                if (password1.equals(confirmpassword1)) {
                                    User_confirmPassword.setErrorEnabled(false);
                                    signup_progress.setVisibility(View.VISIBLE);
                                    Register.setEnabled(false);
                                    firebaseAuth.createUserWithEmailAndPassword(email1, confirmpassword1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (!task.isSuccessful()) {
                                                signup_progress.setVisibility(View.INVISIBLE);
                                                Register.setEnabled(true);
                                                // Sign in success, update UI with the signed-in user's information
                                                try {
                                                    throw task.getException();
                                                } catch (FirebaseAuthUserCollisionException existEmail) {
                                                    User_emailId.setError("Email Exits");
                                                    Email_notNull("Email Exits");
                                                    Register.setEnabled(true);
                                                    // TODO: Take your action
                                                } catch (Exception e) {
                                                    Log.d("Signup_screen", "onComplete: " + e.getMessage());
                                                    Register.setEnabled(true);
                                                }


                                            } else {
                                                currentuser = firebaseAuth.getCurrentUser();
                                                updateUI(username1, email1, phonenumber1);


                                            }

                                        }
                                    });
                                } else {
                                    User_confirmPassword.setError("Confirm password and password must be same");
                                    User_confirmPassword.getEditText().addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            if (s.toString().equals(password1)) {
                                                User_confirmPassword.setErrorEnabled(false);
                                                User_confirmPassword.setError(null);
                                            }
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {

                                        }
                                    });


//                                Username_NotNull("Username not less then 3 character");
                                }

                            } else {
                                User_password.setErrorEnabled(true);
                                User_password.setError("Password must be min 6 character");
                                User_password.getEditText().addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        if(s.length()<6){
                                            User_password.setErrorEnabled(true);
                                            User_password.setError("Password must be min 6  character");
                                        }
                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        if(s.length()>6){
                                            User_password.setErrorEnabled(false);
                                            User_password.setError(null);
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                            }

                        } else {
                            User_emailId.setHelperText("Require email id");
                            Email_notNull("Invalid email");

                        }
                    } else {
                        if (!phonenumber1.matches(String.valueOf(Patterns.PHONE))) {
                            User_phone_number.setErrorEnabled(true);
                            User_phone_number.setError("Invalid Phone Number");
                        }
                        User_phone_number.setErrorEnabled(true);
                        User_phone_number.setError("Require Phone Number");

                    }

                } else {
                    User_username.setError("Full name must be between 6 to 20 character long");
                    if(username1.length()<6){
                        User_username.setErrorEnabled(true);
                        Username_NotNull("Full Name must be min 6 character");
                    }
                    if(username1.length()>20){
                        User_username.setErrorEnabled(true);
                        Username_NotNull("Full Name must be max 20 character");
                    }


                }


//        '''Fields are empty'''
            } else {
                if (username1.isEmpty()) {
                    User_username.setError("Require Full Name");
                    Username_NotNull("Full Name not less than 6 character");


                }
                if (phonenumber1.isEmpty()) {
                    User_phone_number.setError("Require phonenumber");
                    Phonenumber_NotNull("Require Phone number");

                }
                if (email1.isEmpty()) {
                    User_emailId.setError("Require Email id");
                    Email_notNull("Require Email id");


                }
                if (password1.isEmpty()) {
                    User_password.setError("Require Password");
                    User_password.getEditText().addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            if(s.length()==0){
                                User_password.setErrorEnabled(true);
                                User_password.setError("Require Password");
                            }
                            if(s.length()<6){
                                User_password.setErrorEnabled(true);
                                User_password.setError("Password must be min 6  character");
                            }
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                            if(s.length()>6){
                                User_password.setErrorEnabled(false);
                                User_password.setError(null);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });


                }
                if (confirmpassword1.isEmpty()) {
                    User_confirmPassword.setError("Require confirm password");
                    User_confirmPassword.getEditText().addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            User_confirmPassword.setErrorEnabled(false);
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.toString().equals(password1)) {
                                User_confirmPassword.setErrorEnabled(false);
                                User_confirmPassword.setError(null);
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



    private void Username_NotNull(final String error_message ){
        User_username.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                   if (s.length() < 6) {
                    User_username.setError(error_message);
                }
                   if(s.length()>20){
                       User_username.setError("Full name must be less than 20 character");
                   }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 6) {
                    User_username.setErrorEnabled(true);
                    User_username.setError(error_message);
                }

                if (s.length() >=6 && s.length()<=20) {
                    User_username.setError(null);
                    User_username.setEndIconDrawable(R.drawable.ic_baseline_check_circle_24);
                    User_username.setErrorEnabled(false);
                    User_username.setHelperTextEnabled(false);

                }
                if(s.length()>20){
                    User_username.setError("Full name must be less than 20 character");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
    }
    private void Phonenumber_NotNull(final String error_message ){
        User_phone_number.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() < 10) {
                    User_phone_number.setError(error_message);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 10) {
                    User_phone_number.setErrorEnabled(true);

                    User_phone_number.setError(error_message);
                }

                if (s.length() >9) {
                    User_phone_number.setError(null);
                    User_phone_number.setEndIconDrawable(R.drawable.ic_baseline_check_circle_24);
                    User_phone_number.setErrorEnabled(false);
                    User_phone_number.setHelperTextEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
    }
    private void Email_notNull(final String error_message ){
        User_emailId.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!s.toString().matches(emailPattern)) {
                    User_emailId.setErrorEnabled(true);
                    User_emailId.setError(error_message);


                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                     if (s.toString().matches(emailPattern)) {
                            User_emailId.setError(null);
                            User_emailId.setEndIconDrawable(R.drawable.ic_baseline_check_circle_24);
                            User_emailId.setErrorEnabled(false);
                            User_emailId.setHelperTextEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                User_emailId.setEndIconDrawable(R.drawable.ic_baseline_check_circle_24);
            }

        });
    }


    private void updateUI(String Fullname, final String email1, String phonenumber1) {
        Map<String,Object> userdata = new HashMap<>();
        userdata.put("Fullname",Fullname);
        userdata.put("Phonenumber",phonenumber1);
        userdata.put("EmailId",email1);
        db.collection("Registered Users").document(email1).set(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
             if(task.isSuccessful()){
                 SignUpscreenDirections.ActionSignUpscreenToEmailverify actionSignUpscreenToEmailverify = SignUpscreenDirections.actionSignUpscreenToEmailverify();
                 actionSignUpscreenToEmailverify.setEmailId(email1);
                 navController.navigate(actionSignUpscreenToEmailverify);
                 Register.setEnabled(true);
             }else{
                 signup_progress.setVisibility(View.INVISIBLE);
                 Register.setEnabled(true);

             }
            }
        });


    }






}