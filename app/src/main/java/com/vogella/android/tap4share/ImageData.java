package com.vogella.android.tap4share;

/**
 * Created by jonas on 6/19/17.
 */

public class ImageData {

    private String timestamp;
    private String source;
    private String title;
    private String description;
    private String location;

    public ImageData(String timestamp, String source, String title, String description, String location) {
        this.timestamp = timestamp;
        this.source = source;
        this.title = title;
        this.description = description;
        this.location = location;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }
}
