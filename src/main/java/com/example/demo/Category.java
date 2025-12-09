package com.example.demo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;

@Entity
@JsonIgnoreProperties({"recipes"}) // Ignoriert die 'recipes' Liste bei der JSON-Serialisierung, um unendliche Rekursion (StackOverflowError) zu vermeiden.
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // HINZUGEFÜGT: Stellt sicher, dass keine zwei Kategorien den gleichen Namen haben können.
    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Recipe> recipes; // Für die Datenbank-Beziehung

    // Konstruktoren
    public Category() {}
    public Category(String name) { this.name = name; }

    // Getter und Setter
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Recipe> getRecipes() { return recipes; }
    public void setRecipes(List<Recipe> recipes) { this.recipes = recipes; }
}