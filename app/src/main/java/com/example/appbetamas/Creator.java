package com.example.appbetamas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sravan on 1/17/2018.
 */

public class Creator{
    public String key;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public HashMap<String, HashMap<String, Object>> getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(HashMap<String, HashMap<String, Object>> thumbnails) {
        this.thumbnails = thumbnails;
    }

    public HashMap<String, String> getLocalized() {
        return localized;
    }

    public void setLocalized(HashMap<String, String> localized) {
        this.localized = localized;
    }

    public HashMap<String, Object> getStatistics() {
        return statistics;
    }

    public void setStatistics(HashMap<String, Object> statistics) {
        this.statistics = statistics;
    }

    public ArrayList<String> getVideoLinks() {
        return videoLinks;
    }

    public void setVideoLinks (ArrayList<String> videoLinks) {
        this.videoLinks = videoLinks;
    }

    public String country;
    public String customUrl;
    public String description;
    public String date;
    public String subscribers;
    public String videos;
    public String viewers;
    public String thumbnail;
    public String title;
    public String publishedAt;
    public HashMap<String, HashMap<String, Object>> thumbnails;
    public HashMap<String, String> localized;
    public HashMap<String, Object> statistics;
    public ArrayList<String> videoLinks;

    //Create a book object to store all information for displaying and updating a book
    //Server public no argument constructor
    public Creator() {

    }

    public Creator(String key, String country, String customUrl, String description, String date, String subscribers, String videos, String viewers,
                   String thumbnail, String title, String publishedAt, HashMap<String, HashMap<String, Object>> thumbnails, HashMap<String, String> localized, HashMap<String, Object> statistics, ArrayList<String> videoLinks) {
        this.key = key;
        this.country = country;
        this.customUrl = customUrl;
        this.description = description;
        this.date = date;
        this.subscribers = subscribers;
        this.description = description;
        this.videos = videos;
        this.viewers = viewers;
        this.videos = thumbnail;
        this.title = title;
        this.publishedAt = publishedAt;
        this.thumbnails = thumbnails;
        this.localized = localized;
        this.statistics = statistics;
        this.videoLinks = videoLinks;

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCustomUrl() {
        return customUrl;
    }

    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(String subscribers) {
        this.subscribers = subscribers;
    }

    public String getVideos() {
        return videos;
    }

    public void setVideos(String videos) {
        this.videos = videos;
    }

    public String getViewers() {
        return viewers;
    }

    public void setViewers(String viewers) {
        this.viewers = viewers;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
