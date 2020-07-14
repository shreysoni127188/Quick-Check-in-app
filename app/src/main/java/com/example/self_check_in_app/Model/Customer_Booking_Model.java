package com.example.self_check_in_app.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

public class Customer_Booking_Model {
    private String GuestName,RoomType,EmailId;
    private Timestamp Check_in_Date_Time,Check_out_Date_Time;
    private Long No_of_guest;
    @DocumentId
    private String Booking_id;

    public Customer_Booking_Model() {
    }

    public Customer_Booking_Model(String lead_Guest, String room_type, String EmailId, Timestamp check_in_Time_date, Timestamp check_out_Time_date, Long no_of_guest, String booking_id) {
        GuestName = lead_Guest;
        RoomType = room_type;
        this.EmailId = EmailId;
        Check_in_Date_Time = check_in_Time_date;
        Check_out_Date_Time = check_out_Time_date;
        No_of_guest = no_of_guest;
        Booking_id = booking_id;
    }

    public String getGuestName() {
        return GuestName;
    }

    public void setGuestName(String lead_Guest) {
        GuestName = lead_Guest;
    }

    public String getRoomType() {
        return RoomType;
    }

    public void setRoomType(String room_type) {
        RoomType = room_type;
    }





    public Timestamp getCheck_in_Date_Time() {
        return Check_in_Date_Time;
    }

    public void setCheck_in_Date_Time(Timestamp check_in_Time_date) {
        Check_in_Date_Time = check_in_Time_date;
    }

    public Timestamp getCheck_out_Date_Time() {
        return Check_out_Date_Time;
    }

    public void setCheck_out_Date_Time(Timestamp check_out_Time_date) {
        Check_out_Date_Time = check_out_Time_date;
    }

    public Long getNo_of_guest() {
        return No_of_guest;
    }

    public void setNo_of_guest(Long no_of_guest) {
        No_of_guest = no_of_guest;
    }

    public String getBooking_id() {
        return Booking_id;
    }

    public void setBooking_id(String booking_id) {
        Booking_id = booking_id;
    }
//    Email Id Getters & Setters

    public String getEmailId() {
        return EmailId;
    }

    public void setEmailId(String emailId) {
        EmailId = emailId;
    }
}
