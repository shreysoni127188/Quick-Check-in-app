package com.example.self_check_in_app;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
/**
 * This class will give information related to room no and floor no .
 */
public class Check_in extends Fragment implements View.OnClickListener {

    TextView Roomno,Floorno;
    private Network network;
    private FirebaseAuth firebaseAuth;
    private NavController navController;
    private Toolbar toolbar;
    private Button back_to_home;
    public Check_in() {
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
        View view = inflater.inflate(R.layout.fragment_check_in, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String RoomNo = Check_inArgs.fromBundle(getArguments()).getRoomNo();
        String FloorNo = Check_inArgs.fromBundle(getArguments()).getFloorNo();
        firebaseAuth = FirebaseAuth.getInstance();
        Roomno = view.findViewById(R.id.RoomNo);
        Floorno = view.findViewById(R.id.FloorNo);
        Roomno.setText(RoomNo);
        Floorno.setText(FloorNo);
        network = new Network(getContext());
        navController = Navigation.findNavController(view);
        view.findViewById(R.id.Logout).setOnClickListener(this);
        back_to_home = view.findViewById(R.id.BackTo_home_btn);
        back_to_home.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Logout:
                Logout();
                break;
            case R.id.BackTo_home_btn:
                BackToHome();
                break;
        }
        }

    private void BackToHome() {
        navController.navigate(R.id.action_check_in_to_mainpage);
    }


    private void Logout() {
            if(network.isConnectingToInternet()){

                firebaseAuth.signOut();
                navController.navigate(R.id.action_check_in_to_loginscreen);
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        Roomno.setText("");
        Floorno.setText("");
    }
}