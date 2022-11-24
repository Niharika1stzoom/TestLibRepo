package com.firstzoom.bluevision.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CameraInfo implements Serializable {
    UUID id;
    String name,description;
    @SerializedName("image")
    String image_url;
    List<String> recordings;
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage_url() {
        return image_url;
    }

    public List<String> getRecordings() {
        return recordings;
    }


}
