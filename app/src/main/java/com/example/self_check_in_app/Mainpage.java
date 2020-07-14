package com.example.self_check_in_app;


import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.self_check_in_app.Model.UserHistoryModel;
import com.example.self_check_in_app.ViewModel.UserHistoryViewModel;
import com.example.self_check_in_app.adapter.UserHistoryViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;



/**
 * Here user will be allow to enter Booking id .

 */
public class Mainpage extends Fragment implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private Network network;
    private NavController navController;
    private TextInputEditText Booking_id;
    private TextInputLayout Booking_id_input_box;
    private Button NextBtn;
    private FirebaseFirestore firebaseFirestore;
    private String BookingId;
    private ProgressBar progressBar;
    private String Useremailid;
    private RecyclerView UserHistoryRecyclerView ;
    private UserHistoryViewAdapter userHistoryViewAdapter;
    private ImageView no_data;
    private TextView check_data;
    private ProgressBar no_data_progressbar;
    private UserHistoryViewModel userHistoryViewModel;

    public Mainpage() {
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
        return inflater.inflate(R.layout.fragment_mainpage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InputMethodManager inputMethod = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethod.hideSoftInputFromWindow(getView().getWindowToken(),0);
//        Firebase and Navcontroller
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        navController = Navigation.findNavController(view);
//        Field initalization
        Booking_id = view.findViewById(R.id.Booking_Id_edit_text);
        Booking_id_input_box = view.findViewById(R.id.Booking_Id_InputLayout);
        NextBtn = view.findViewById(R.id.Back_Btn);
        progressBar = view.findViewById(R.id.Mainpage_progressBar);
        Useremailid = firebaseAuth.getCurrentUser().getEmail();
        no_data_progressbar = view.findViewById(R.id.no_data_progressBar2);
        no_data = view.findViewById(R.id.no_data_search);
        check_data = view.findViewById(R.id.check_data);

        UserHistoryRecyclerView = view.findViewById(R.id.UserHistoryRecyclerView);
        userHistoryViewAdapter = new UserHistoryViewAdapter();
        UserHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        UserHistoryRecyclerView.setHasFixedSize(true);

//        Button click listener
        view.findViewById(R.id.Logout).setOnClickListener(this);
        NextBtn.setOnClickListener(this);
//        Progressbar
        progressBar.setVisibility(View.INVISIBLE);
        no_data_progressbar.setVisibility(View.VISIBLE);
        no_data.setVisibility(View.INVISIBLE);
        check_data.setVisibility(View.INVISIBLE);


    }




    /**
     * Once activity is created we will fetch live data of user previous check-in details.
     * Because of mutable live data we have observer which will observe data in firebase in background and notify adapter if any
     * data added.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        no_data_progressbar.setVisibility(View.VISIBLE);
        final String currentuser = firebaseAuth.getCurrentUser().getEmail();
       userHistoryViewModel = new ViewModelProvider(getActivity()).get(UserHistoryViewModel.class);
        userHistoryViewModel.getSize1(currentuser).observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(final Integer integer) {
                if(integer!=0){
                    no_data_progressbar.setVisibility(View.VISIBLE);
                    userHistoryViewModel.getUserHistoryData(currentuser).observe(getViewLifecycleOwner(), new Observer<List<UserHistoryModel>>() {
                        @Override
                        public void onChanged(List<UserHistoryModel> userHistoryModels) {
                                if (userHistoryModels.size()!=0) {
                                    no_data_progressbar.setVisibility(View.INVISIBLE);
                                    no_data.setVisibility(View.INVISIBLE);
                                    check_data.setVisibility(View.INVISIBLE);
                                    UserHistoryRecyclerView.setAdapter(userHistoryViewAdapter);
                                    userHistoryViewAdapter.setUserHistoryModels(userHistoryModels);
                                    userHistoryViewAdapter.notifyDataSetChanged();

                                }


                        }
                    });
                }else{
                    no_data_progressbar.setVisibility(View.VISIBLE);
                    userHistoryViewAdapter.setUserHistoryModels(null);
                    userHistoryViewAdapter.notifyDataSetChanged();
                    no_data_progressbar.setVisibility(View.INVISIBLE);
                    no_data.setVisibility(View.VISIBLE);
                    check_data.setVisibility(View.VISIBLE);

                }
            }
        });






}


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Logout:
                Logout();
                break;
            case R.id.Back_Btn:
                NextBtn();
                break;

        }
    }
    /**
     * Booking id must be 6 character long.
     * If user is already check-in its detail will be show in check-in history.
     */
    private void NextBtn() {
        BookingId  = Booking_id.getText().toString();
        if (BookingId.length() == 6) {
            progressBar.setVisibility(View.VISIBLE);
            NextBtn.setEnabled(false);
            Booking_id_input_box.setErrorEnabled(false);
            firebaseFirestore.collection("Check-in Details").document(BookingId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()&&task.getResult().exists()) {
                        if (Useremailid.equals(task.getResult().getString("Email_id"))) {
                            progressBar.setVisibility(View.INVISIBLE);
                            NextBtn.setEnabled(true);
                            Booking_id_input_box.setError("Booking Id already checked-in");
                        }else {
                            progressBar.setVisibility(View.INVISIBLE);
                            NextBtn.setEnabled(true);
                            Booking_id_input_box.setError("Invalid Booking-id");
                        }
                    }else{
                        getBookingDetails();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });


    }else{
            Booking_id_input_box.setError("Booking Id must be 6 character long");

        }
    }

    private void getBookingDetails() {

        firebaseFirestore.collection("Booking Details").document(BookingId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressBar.setVisibility(View.VISIBLE);
                NextBtn.setEnabled(false);
                if (task.isSuccessful()) {
                    String emailid = task.getResult().getString("EmailId");
                    String Available = task.getResult().getString("Available");
                    if(Useremailid.equals(emailid)) {
                        if (Available.startsWith("Y")) {
                            progressBar.setVisibility(View.VISIBLE);
                            NextBtn.setEnabled(false);
                            InputMethodManager inputMethod = (InputMethodManager)getView().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethod.hideSoftInputFromWindow(getView().getWindowToken(),0);
                            MainpageDirections.ActionMainpageToCustomerBookingDetails actionMainpageToCustomerBookingDetails = MainpageDirections.actionMainpageToCustomerBookingDetails();
                            actionMainpageToCustomerBookingDetails.setBookingId(Booking_id.getText().toString());
                            navController.navigate(actionMainpageToCustomerBookingDetails);
                        }else{
                            progressBar.setVisibility(View.INVISIBLE);
                            NextBtn.setEnabled(true);
                            Booking_id_input_box.setError("User Checked-in");
                        }
                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                        NextBtn.setEnabled(true);
                        Booking_id_input_box.setError("Invalid booking-id");
                    }
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    NextBtn.setEnabled(true);
                    Booking_id_input_box.setError("Try again");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    private void Logout() {
        if(network.isConnectingToInternet()){
        no_data_progressbar.setVisibility(View.VISIBLE);
        userHistoryViewAdapter.setUserHistoryModels(null);
        userHistoryViewAdapter.notifyDataSetChanged();
        no_data_progressbar.setVisibility(View.INVISIBLE);
        firebaseAuth.signOut();
        navController.navigate(R.id.action_mainpage_to_loginscreen);
                }
        else {
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