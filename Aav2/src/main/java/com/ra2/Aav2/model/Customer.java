package com.ra2.Aav2.model;

public class Customer {
    private long id;
    private String name;
    private String description;
    private int age;
    private String course;
    private int dataCreated;
    private int dataUpdated;
    private String imagePath; // Nuevo campo para la ruta de imagen
    private String password;

    public Customer() {}

    public Customer(long id, String name, String description, int age, String course,
                    int dataCreated, int dataUpdated, String imagePath, String password) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.age = age;
        this.course = course;
        this.dataCreated = dataCreated;
        this.dataUpdated = dataUpdated;
        this.imagePath = imagePath;
        this.password = password;
    }

    // Getters y setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public int getDataCreated() { return dataCreated; }
    public void setDataCreated(int dataCreated) { this.dataCreated = dataCreated; }

    public int getDataUpdated() { return dataUpdated; }
    public void setDataUpdated(int dataUpdated) { this.dataUpdated = dataUpdated; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, name='%s', description='%s', age='%d', course='%s', dateCreated='%d', dateUpdated='%d', imagePath='%s', password='%s']",
                id, name, description, age, course, dataCreated, dataUpdated, imagePath, password);
    }
}
