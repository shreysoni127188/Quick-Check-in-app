package com.example.self_check_in_app;


import android.graphics.Bitmap;


import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import android.view.KeyEvent;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;


import android.widget.TextView;

import android.widget.Toolbar;

import com.example.self_check_in_app.Model.RoomAvaibilityModel;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;



import java.io.ByteArrayOutputStream;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Here we will verify user document.
 * Because of some error document cannot be uploded.
 * verify document feature is disable for temporary .
 *
 * User needs to provide signature in order to check-in.
 * After providing signature we will check is there any room available as per user need,if yes we will allocate room to user.
 * once room is allocated that particular room will be in not available state.
 */

public class Verifydocument extends Fragment implements View.OnClickListener {
    private Toolbar toolbar;
    private String booking_id;
    private SignaturePad signaturePad;
    private Button CheckIn;
    private FirebaseStorage mstorageRef;
    private FirebaseAuth firebaseAuth;
    private Button Clear, ClickImage, UploadImage;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private String RoomType;
    private Long No_of_guest;
    private NavController navController;
    private String SignatureUrl;
    private TextView providesignature;

    public void Verifydocument() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verifydocument, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, null);
        booking_id = VerifydocumentArgs.fromBundle(getArguments()).getBookingId();
        RoomType = VerifydocumentArgs.fromBundle(getArguments()).getRoomType();
        No_of_guest = VerifydocumentArgs.fromBundle(getArguments()).getNoOfGuest();
//        Signature Pad
        signaturePad = view.findViewById(R.id.signaturePad2);
        providesignature = view.findViewById(R.id.ProvideSignature);
//        Check-in,Clear,UploadImage,ClickImage
        CheckIn = view.findViewById(R.id.Check_in_verify_document);
        ClickImage = view.findViewById(R.id.Click_btn);
        UploadImage = view.findViewById(R.id.Upload_Btn);
        Clear = view.findViewById(R.id.Clear_Sign);
//        Firebase
        mstorageRef = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("Room Availability").document().getParent();
//        UserImage
        ClickImage.setEnabled(false);
        UploadImage.setEnabled(false);

//        Navcontroller
        navController = Navigation.findNavController(view);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(null);
        ClickImage.setOnClickListener(this);
        UploadImage.setOnClickListener(this);
        Clear.setOnClickListener(this);
        CheckIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Check_in_verify_document:
                CheckIn();
                break;
            case R.id.Clear_Sign:
                ClearSignature();
                break;
        }
    }




    private void ClearSignature() {
        signaturePad.clear();
    }

    private void CheckIn() {
        if (!signaturePad.isEmpty()) {
            providesignature.setTextColor(Integer.parseInt(String.valueOf(getResources().getColor(R.color.colorPrimary))));
            CheckForRoomAvailability();
        } else {
            providesignature.setTextColor(Color.RED);

        }

    }

    private void CheckForRoomAvailability() {
        CheckIn.setEnabled(false);
        Clear.setEnabled(false);

        final Query Roomavailable = collectionReference.whereEqualTo("Available","Y").whereEqualTo("Room_type",RoomType).whereEqualTo("Room_size",No_of_guest).limit(1);
        Roomavailable.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()&&task.getResult().size()==1){

                    List<RoomAvaibilityModel> roomAvaibilityModel1 = task.getResult().toObjects(RoomAvaibilityModel.class);
                    for(RoomAvaibilityModel roomAvaibilityModel : roomAvaibilityModel1){
                        String roomid = roomAvaibilityModel.getRoom_documnet_id();
                        String Roomtype = roomAvaibilityModel.getRoom_type();
                        Long Roomsize = roomAvaibilityModel.getRoom_size();
                        Long Floorno = roomAvaibilityModel.getFloor_no();
                        Long Roomno = roomAvaibilityModel.getRoom_no();
                        UploadUserSignatureProof(roomid);
                        UploadDetails(Roomtype, Roomsize, Floorno, Roomno);
                    }

                } else {
                    CheckIn.setEnabled(true);
                    Clear.setEnabled(true);


                    Snackbar.make(getView(),"No rooms are available",BaseTransientBottomBar.LENGTH_LONG).setAction("Okay", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
//
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }




    private void MakeRoomNotAvailable(String roomId) {

        collectionReference.document(roomId).update("Available", "N");
        firebaseFirestore.collection("Booking Details").document(booking_id).update("Available","N");
    }
    private void UploadUserSignatureProof(final String roomid) {
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {

                        return true;
                    }
                }
                return false;
            }
        });
        final String random = UUID.randomUUID().toString();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        signaturePad.getSignatureBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        final StorageReference storageReference = mstorageRef.getReference();
        storageReference.child(firebaseAuth.getCurrentUser().getEmail()).child("signature" + random + ".jpg").putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null) {
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                CheckIn.setEnabled(false);
                                SignatureUrl = uri.toString();
                                firebaseFirestore.collection("Check-in Details").document(booking_id).update("SignatureProof",SignatureUrl);
                                MakeRoomNotAvailable(roomid);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Clear.setEnabled(true);
                                CheckIn.setEnabled(true);

                            }
                        });
                    }
                }
            }
        });
    }
    private void UploadDetails(String roomtype, Long roomsize, final Long floorno, final Long roomno) {

        final Map<String, Object> check_in_details = new HashMap<>();
        check_in_details.put("Check_in_status", "Y");
        check_in_details.put("Check_in_date_time", FieldValue.serverTimestamp());
        check_in_details.put("RoomNo",roomno);
        check_in_details.put("RoomType",roomtype);
        check_in_details.put("RoomSize",roomsize);
        check_in_details.put("FloorNo",floorno);
        check_in_details.put("Email_id",firebaseAuth.getCurrentUser().getEmail());
        check_in_details.put("SignatureProof",SignatureUrl);
        firebaseFirestore.collection("Check-in Details").document(booking_id).set(check_in_details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    CheckIn.setEnabled(false);
                    Clear.setEnabled(false);

                    VerifydocumentDirections.ActionVerifydocumentToCheckIn actionVerifydocumentToCheckIn = VerifydocumentDirections.actionVerifydocumentToCheckIn();
                    actionVerifydocumentToCheckIn.setRoomNo(String.valueOf(roomno));
                    actionVerifydocumentToCheckIn.setFloorNo(String.valueOf(floorno));
                    navController.navigate(actionVerifydocumentToCheckIn);

                } else {
                    Snackbar.make(getView(),"Check-in fail",BaseTransientBottomBar.LENGTH_LONG).setAction("Okay", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                    CheckIn.setEnabled(true);
                    Clear.setEnabled(true);

                }
            }
        });
    }





}