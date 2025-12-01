package com.example.demo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;

@Entity
@JsonIgnoreProperties({"recipes"})
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @OneToMany(mappedBy = "category")
    private List<Recipe> recipes;

    public Category() {}

    public Category(String name) {
        this.name = name;
    }
        public Long getId() { return id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<Recipe> getRecipes() { return recipes; }
        public void setRecipes(List<Recipe> recipes) { this.recipes = recipes; }
    }