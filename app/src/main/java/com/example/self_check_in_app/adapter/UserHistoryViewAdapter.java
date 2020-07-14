package com.example.self_check_in_app.adapter;

import android.app.Dialog;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import android.widget.TextView;



import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.self_check_in_app.R;
import com.example.self_check_in_app.Model.UserHistoryModel;
import com.google.firebase.Timestamp;


import java.text.SimpleDateFormat;
import java.util.List;

public class UserHistoryViewAdapter extends RecyclerView.Adapter<UserHistoryViewAdapter.UserHistoryViewHolder>{
    private List<UserHistoryModel> userHistoryModels;
    private Dialog UserHistoryPopUp;
    private TextView bookingid_pop_up,check_in_date_time_pop_up,room_no, floor_no ,room_type;
    private Button close_btn;



    public void setUserHistoryModels(List<UserHistoryModel> userHistoryModels) {
        this.userHistoryModels = userHistoryModels;
    }

    @NonNull
    @Override
    public UserHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_history,parent,false);
        UserHistoryPopUp = new Dialog(view.getContext());
        UserHistoryPopUp.setContentView(R.layout.user_history_pop_up);
        bookingid_pop_up = UserHistoryPopUp.findViewById(R.id.BookingId);
        check_in_date_time_pop_up = UserHistoryPopUp.findViewById(R.id.check_in_date_time_pop_up);
        room_no = UserHistoryPopUp.findViewById(R.id.Room_no_pop_up);
        floor_no = UserHistoryPopUp.findViewById(R.id.Floor_no_pop_up);
        room_type = UserHistoryPopUp.findViewById(R.id.Room_Type_pop_up);
        close_btn = UserHistoryPopUp.findViewById(R.id.Close_btn);

        return new UserHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHistoryViewHolder holder, final int position) {
        final String bookingid = userHistoryModels.get(position).getBookingId();
        final Timestamp check_in_d_t = userHistoryModels.get(position).getCheck_in_date_time();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        holder.bookingid.setText(bookingid);
        holder.showdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookingid_pop_up.setText(bookingid);
                check_in_date_time_pop_up.setText(sdf.format(check_in_d_t.toDate()));
                room_no.setText(String.valueOf(userHistoryModels.get(position).getRoomNo()));
                floor_no.setText(String.valueOf(userHistoryModels.get(position).getFloorNo()));
                room_type.setText(userHistoryModels.get(position).getRoomType());
                UserHistoryPopUp.show();
                close_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserHistoryPopUp.dismiss();
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {

        if(userHistoryModels==null){
            return 0;
        }
        else {

        return userHistoryModels.size();}
    }

    public class UserHistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView bookingid;
        private Button showdetails;


        public UserHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingid = itemView.findViewById(R.id.Booking_id_textview);
            showdetails = itemView.findViewById(R.id.Show_booking_details);

        }



    }
}

