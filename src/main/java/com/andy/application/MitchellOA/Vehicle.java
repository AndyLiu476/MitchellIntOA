package com.andy.application.MitchellOA;

import lombok.*;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Entity;

@Entity
@Data
// POJO (Plan Old Java Object)
public class Vehicle {
    @Id
    @GeneratedValue
    private int id;
    private int year;
    private String make;
    private String model;

    public Vehicle() {
    }

    public Vehicle(int year, String make, String model) {
        this.year = year;
        this.make = make;
        this.model = model;
    }

    // generated getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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
}
