package com.example.appbetamas;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sravan on 1/17/2018.
 */

public class Investor {

    public String userId;
    public String videoKey;
    public String videoName;
    public String percent;
    public String value;

    //Create a book object to store all information for displaying and updating a book
    //Server public no argument constructor
    public Investor() {

    }

    public Investor(String userId, String videoKey, String videoName, String percent, String value) {
        this.userId = userId;
        this.videoKey = videoKey;
        this.videoName = videoName;
        this.percent = percent;
        this.value = value;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVideoKey() {
        return videoKey;
    }

    public void setVideoKey(String videoKey) {
        this.videoKey = videoKey;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}