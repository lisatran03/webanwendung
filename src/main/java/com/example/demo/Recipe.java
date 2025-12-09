package com.example.demo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

// KEINE List<String> mehr
// import java.util.List;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer time;
    private String difficulty;

    // KORREKTUR: Zutaten als langer String speichern, wie vom Frontend gesendet
    @Column(columnDefinition = "TEXT")
    private String ingredients;

    // KORREKTUR: Anweisungen als langer String speichern
    @Column(columnDefinition = "TEXT")
    private String instructions;

    @ManyToOne(fetch = FetchType.EAGER) // Füge FetchType hinzu (empfohlen)
    @JoinColumn(name = "category_id")
    private Category category;

    public Recipe() {}

    // --- Getter und Setter (aktualisiert auf String) ---

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // KORRIGIERT: Zutaten ist jetzt String
    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    // KORRIGIERT: Anweisungen ist jetzt String
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public void setId(Long id) { this.id = id; }

    public Integer getTime() { return time; }
    public void setTime(Integer time) { this.time = time; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}

