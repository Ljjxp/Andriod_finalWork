package com.example.ljj.finalminidowyinapp.bean;

import com.google.gson.annotations.SerializedName;

public class Feed {
    @SerializedName("student_id")
    String student_id;
    @SerializedName("user_name")
    String userName;
    @SerializedName("image_url")
    String image_url;
    @SerializedName("video_url")
    String video_url;

    public String getStudent_id(){
        return student_id;
    }
    public String getUserName(){
        return userName;
    }
    public String getImage_url(){
        return image_url;
    }
    public String getVideo_url(){
        return video_url;
    }
}
