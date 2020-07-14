package com.example.self_check_in_app.Model;

import com.google.firebase.firestore.DocumentId;

public class RoomAvaibilityModel {
    @DocumentId
    private String Room_documnet_id;
    private Long Room_size;
    private Long Floor_no;
    private String Room_type;
    private Long Room_no;
    public RoomAvaibilityModel() {
    }

    public RoomAvaibilityModel(String room_documnet_id, Long room_size, Long floor_no, String room_type, Long Room_no) {
        Room_documnet_id = room_documnet_id;
        Room_size = room_size;
        Floor_no = floor_no;
        Room_type = room_type;
        Room_no = Room_no;
    }

    public String getRoom_documnet_id() {
        return Room_documnet_id;
    }

    public void setRoom_documnet_id(String room_documnet_id) {
        Room_documnet_id = room_documnet_id;
    }

    public Long getRoom_size() {
        return Room_size;
    }

    public void setRoom_size(Long room_size) {
        Room_size = room_size;
    }

    public Long getFloor_no() {
        return Floor_no;
    }

    public void setFloor_no(Long floor_no) {
        Floor_no = floor_no;
    }

    public String getRoom_type() {
        return Room_type;
    }

    public void setRoom_type(String room_type) {
        Room_type = room_type;
    }

    public Long getRoom_no() {
        return Room_no;
    }

    public void setRoom_no(Long room_no) {
        Room_no = room_no;
    }
}
