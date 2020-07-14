package com.example.self_check_in_app.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

public class UserHistoryModel {
    @DocumentId
    private String BookingId;
    private String Check_in_status;
    private Long RoomNo,FloorNo;
    private String RoomType;
    private com.google.firebase.Timestamp Check_in_date_time;

    public UserHistoryModel() {
    }

    public UserHistoryModel(String bookingId, String check_in_status, Long roomNo, Long floorNo, String roomType, Timestamp check_in_date_time) {
        BookingId = bookingId;
        Check_in_status = check_in_status;
        RoomNo = roomNo;
        FloorNo = floorNo;
        RoomType = roomType;
        Check_in_date_time = check_in_date_time;
    }

    public String getBookingId() {
        return BookingId;
    }

    public void setBookingId(String bookingId) {
        BookingId = bookingId;
    }

    public String getCheck_in_status() {
        return Check_in_status;
    }

    public void setCheck_in_status(String check_in_status) {
        Check_in_status = check_in_status;
    }

    public com.google.firebase.Timestamp getCheck_in_date_time() {
        return Check_in_date_time;
    }

    public void setCheck_in_date_time(com.google.firebase.Timestamp check_in_date_time) {
        Check_in_date_time = check_in_date_time;
    }

    public Long getRoomNo() {
        return RoomNo;
    }

    public void setRoomNo(Long roomNo) {
        RoomNo = roomNo;
    }

    public Long getFloorNo() {
        return FloorNo;
    }

    public void setFloorNo(Long floorNo) {
        FloorNo = floorNo;
    }

    public String getRoomType() {
        return RoomType;
    }

    public void setRoomType(String roomType) {
        RoomType = roomType;
    }
}
