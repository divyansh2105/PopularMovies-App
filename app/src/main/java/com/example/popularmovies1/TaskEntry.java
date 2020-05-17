package com.example.popularmovies1;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "favorites")
public class TaskEntry {

    @NonNull
    @PrimaryKey
    private String id;
    private String title;



    @Ignore
    public TaskEntry(String title) {
        this.title = title;

    }

    public TaskEntry(String id, String title) {
        this.id = id;
        this.title = title;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
