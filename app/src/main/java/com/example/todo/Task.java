package com.example.todo;

import java.sql.Time;
import java.sql.Date;

public class Task {
    protected long id;
    protected String title;
    protected String category;
    protected Date date;
    protected Time time;
    protected String description;

    protected String status;

    public Task(long id, String title, String category, Date date, Time time, String description, String status) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.date = date;
        this.time = time;
        this.description = description;
        this.status = status;
    }

    public Task() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Constructors, getters, and setters for the fields
}