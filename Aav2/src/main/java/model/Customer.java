package model;

public class Customer {
    private long id;
    private String name;
    private String description;
    private int age;
    private String course;
    private int dataCreated;
    private int dataUpdated;


    public Customer() {}

    public Customer(long id,String name,String description, int age,String course,int dataCreated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.age = age;
        this.course = course;
        this.dataCreated = dataCreated;
        this.dataUpdated = dataUpdated;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, name='%s', description='%s', age='%d', course='%s', dateCreated='%d', dateUpdated='%d']",
                id, name, description, age, course, dataCreated, dataUpdated);
    }
}


