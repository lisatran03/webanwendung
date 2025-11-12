package com.example.demo;

import jakarta.persistence.*;
import java.util.List;

public class Category {

    private String name;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    // Getter & Setter

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
