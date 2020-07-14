package com.example.self_check_in_app.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.self_check_in_app.FirebaseRepository.FirebaseRespositary;
import com.example.self_check_in_app.Model.UserHistoryModel;

import java.util.List;

public class UserHistoryViewModel extends ViewModel implements FirebaseRespositary.OnFirestoreTaskCompleted {
    private MutableLiveData<List<UserHistoryModel>> UserHistoryData = new MutableLiveData<List<UserHistoryModel>>();
    private MutableLiveData<Integer> size1 = new MutableLiveData<>();




    public UserHistoryViewModel(){

    }

    public LiveData<Integer> getSize1(String emailId) {
        firebaseRespositary.getSize(emailId);
        return size1;
    }

    public LiveData<List<UserHistoryModel>> getUserHistoryData(String emaild) {
        firebaseRespositary.getData(emaild);
        return UserHistoryData;
    }


    private FirebaseRespositary firebaseRespositary = new FirebaseRespositary(this);

    @Override
    public void UserCheckedInDetails(List<UserHistoryModel> userHistoryViewModels) {
            UserHistoryData.setValue(userHistoryViewModels);
    }

    @Override
    public void FirestoreError(Exception e) {

    }

    @Override
    public void UserHistorySize(int size) {
        size1.setValue(size);
    }




}
