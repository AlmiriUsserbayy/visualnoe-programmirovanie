package com.example.studentmgmt.model;

import javafx.beans.property.*;

public class Student {
    private final StringProperty id;
    private final StringProperty fullName;
    private final StringProperty group;
    private final DoubleProperty gpa;

    public Student(String id, String fullName, String group, double gpa) {
        this.id = new SimpleStringProperty(id);
        this.fullName = new SimpleStringProperty(fullName);
        this.group = new SimpleStringProperty(group);
        this.gpa = new SimpleDoubleProperty(gpa);
    }

    public String getId() { return id.get(); }
    public StringProperty idProperty() { return id; }
    public void setId(String id) { this.id.set(id); }

    public String getFullName() { return fullName.get(); }
    public StringProperty fullNameProperty() { return fullName; }
    public void setFullName(String fullName) { this.fullName.set(fullName); }

    public String getGroup() { return group.get(); }
    public StringProperty groupProperty() { return group; }
    public void setGroup(String group) { this.group.set(group); }

    public double getGpa() { return gpa.get(); }
    public DoubleProperty gpaProperty() { return gpa; }
    public void setGpa(double gpa) { this.gpa.set(gpa); }
}