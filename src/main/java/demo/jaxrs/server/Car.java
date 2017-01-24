package demo.jaxrs.server;

/**
 * Created by guxm on 2017/1/20.
 */
public class Car {
    private long id;
    private String make;
    private String model;
    private String color;
    private String weight;
    private int year;

    public Car() {
    }

    public Car(long id, String make, String model, String color, String weight, int year) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.color = color;
        this.weight = weight;
        this.year = year;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
