package com.ra2.Aav2.model;

import java.sql.Timestamp;

public class Customer {

    private Long id;
    private String name;
    private String description;
    private Integer age;
    private String course;
    private String email;
    private String password;
    private String imagePath;
    private Timestamp dataCreated;
    private Timestamp dataUpdated;

    public Customer() { }

    public Customer(Long id, String name, String description, Integer age, String course,
                    String email, String password, String imagePath, Timestamp dataCreated, Timestamp dataUpdated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.age = age;
        this.course = course;
        this.email = email;
        this.password = password;
        this.imagePath = imagePath;
        this.dataCreated = dataCreated;
        this.dataUpdated = dataUpdated;
    }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Timestamp getDataCreated() { return dataCreated; }
    public void setDataCreated(Timestamp dataCreated) { this.dataCreated = dataCreated; }

    public Timestamp getDataUpdated() { return dataUpdated; }
    public void setDataUpdated(Timestamp dataUpdated) { this.dataUpdated = dataUpdated; }
}
