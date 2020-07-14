package com.example.self_check_in_app.FirebaseRepository;


import androidx.annotation.Nullable;


import com.example.self_check_in_app.Model.UserHistoryModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;



import java.util.List;

public class FirebaseRespositary {
    private OnFirestoreTaskCompleted onFirestoreTaskCompleted;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("Check-in Details");
    public FirebaseRespositary() {
    }

    public FirebaseRespositary(OnFirestoreTaskCompleted onFirestoreTaskCompleted) {
        this.onFirestoreTaskCompleted = onFirestoreTaskCompleted;
    }
    public void getSize(String emailid){
        Query checkin = collectionReference.whereEqualTo("Email_id",emailid);
        checkin.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e==null){
                    onFirestoreTaskCompleted.UserHistorySize(queryDocumentSnapshots.size());
                }
            }
        });
    }
    public void getData(String emailid){
        collectionReference.whereEqualTo("Email_id",emailid).orderBy("Check_in_date_time", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if(e==null) {
                        if (!queryDocumentSnapshots.isEmpty()&&queryDocumentSnapshots.size()>0) {

                            for(DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()){
                                if(documentChange.getType()==DocumentChange.Type.ADDED){
                                    onFirestoreTaskCompleted.UserCheckedInDetails(queryDocumentSnapshots.toObjects(UserHistoryModel.class));
                                }
                            }

                        }
                    }
                    onFirestoreTaskCompleted.FirestoreError(e);
            }
        });

    }
    public interface OnFirestoreTaskCompleted{
        void UserCheckedInDetails(List<UserHistoryModel> userHistoryModels);
        void FirestoreError(Exception e);
        void UserHistorySize(int size);
    }

}
