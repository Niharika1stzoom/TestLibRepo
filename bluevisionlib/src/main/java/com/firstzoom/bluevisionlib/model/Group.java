package com.firstzoom.bluevisionlib.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Group implements Serializable {
    UUID id;
    String name,description;
    List<UUID> feeds;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<UUID> getFeeds() {
        return feeds;
    }
}
