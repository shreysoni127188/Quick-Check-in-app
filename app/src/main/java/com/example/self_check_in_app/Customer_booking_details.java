package com.example.self_check_in_app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.self_check_in_app.Model.Customer_Booking_Model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

/**
 * Here we will ge customer booking details.
 */
public class Customer_booking_details extends Fragment implements View.OnClickListener {
    private String Booking_Id;
    private TextView booking_id,lead_guest,email_id,no_of_guest,check_in_date_time,check_out_date_time,room_type;
    private Button Next_btn;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private Customer_Booking_Model customer_booking_model;
    private Toolbar toolbar;
    private NavController navController;
    private String Roomtype;
    private Long No_of_guest;
    private ProgressBar progressBar;

    public Customer_booking_details() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_booking_details, container, false);
        toolbar = view.findViewById(R.id.toolbar2);
        return view;
    }
    /**
     * After view is created we will call getDetails function to get all the details of user booking.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        NavController
        navController = Navigation.findNavController(view);
//        Toolbar
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
//        Field initalization
        Booking_Id = Customer_booking_detailsArgs.fromBundle(getArguments()).getBookingId();
        booking_id = view.findViewById(R.id.Booking_Id);
        lead_guest = view.findViewById(R.id.LeadGuestName);
        email_id = view.findViewById(R.id.Customer_Email_id);
        no_of_guest = view.findViewById(R.id.Customer_No_Guest);
        check_in_date_time = view.findViewById(R.id.Check_in_D_And_T);
        check_out_date_time = view.findViewById(R.id.Check_out_D_And_T);
        room_type = view.findViewById(R.id.Customer_Room_Type);
        Next_btn = view.findViewById(R.id.Next_Btn);
//        Progressbar
        progressBar = view.findViewById(R.id.Bookingdetails_progressBar);
        progressBar.setVisibility(View.VISIBLE);
//        Firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
//        Button Listener
        Next_btn.setEnabled(false);
        Next_btn.setOnClickListener(this);
//      Toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
//        Get details of booking id
        getDetails();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                    return true;
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.Next_Btn:
                    NextPage();
                    break;

            }
    }


    private void getDetails(){
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        firebaseFirestore.collection("Booking Details").document(Booking_Id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Next_btn.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    customer_booking_model = task.getResult().toObject(Customer_Booking_Model.class);
                    booking_id.setText(Booking_Id);
                    lead_guest.setText(customer_booking_model.getGuestName());
                    email_id.setText(customer_booking_model.getEmailId());
                    No_of_guest = customer_booking_model.getNo_of_guest();
                    no_of_guest.setText(No_of_guest.toString());
                    check_in_date_time.setText(sdf.format(customer_booking_model.getCheck_in_Date_Time().toDate()));
                    check_out_date_time.setText(sdf.format(customer_booking_model.getCheck_out_Date_Time().toDate()));
                    Roomtype = customer_booking_model.getRoomType();
                    room_type.setText(Roomtype);
                }else{
                    Next_btn.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    private void NextPage(){
        Customer_booking_detailsDirections.ActionCustomerBookingDetailsToVerifydocument actionCustomerBookingDetailsToVerifydocument = Customer_booking_detailsDirections.actionCustomerBookingDetailsToVerifydocument();
        actionCustomerBookingDetailsToVerifydocument.setBookingId(Booking_Id);
        actionCustomerBookingDetailsToVerifydocument.setRoomType(Roomtype);
        actionCustomerBookingDetailsToVerifydocument.setNoOfGuest(No_of_guest);
        navController.navigate(actionCustomerBookingDetailsToVerifydocument);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }
}
