package kz.atu.lab04;

public class Student {
    private final String fullName;
    private final String group;
    private final String course;

    public Student(String fullName, String group, String course) {
        this.fullName = fullName;
        this.group = group;
        this.course = course;
    }

    public String getFullName() { return fullName; }
    public String getGroup() { return group; }
    public String getCourse() { return course; }
}
