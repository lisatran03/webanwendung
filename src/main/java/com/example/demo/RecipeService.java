package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepo;
    private final CategoryRepository categoryRepo;

    // Dependency Injection über Konstruktor
    @Autowired
    public RecipeService(RecipeRepository recipeRepo, CategoryRepository categoryRepo) {
        this.recipeRepo = recipeRepo;
        this.categoryRepo = categoryRepo;
    }
    /**
     * Geschäftslogik: Speichert ein Rezept, kümmert sich um die zugehörige Kategorie.
     * Dies ist die Logik, die aus dem Controller (setCategoryIfExists) verschoben wurde.
     */
    @Transactional
    public Recipe saveRecipe(Recipe recipe) {
        setCategory(recipe);
        return recipeRepo.save(recipe);
    }

    /**
     * Geschäftslogik: Aktualisiert ein bestehendes Rezept (PUT-Logik aus Controller).
     */
    @Transactional
    public Optional<Recipe> updateRecipe(Long id, Recipe updatedRecipe) {
        return recipeRepo.findById(id).map(existing -> {
            // Update der Felder
            existing.setName(updatedRecipe.getName());
            existing.setInstructions(updatedRecipe.getInstructions());
            existing.setIngredients(updatedRecipe.getIngredients());
            existing.setTime(updatedRecipe.getTime());
            existing.setDifficulty(updatedRecipe.getDifficulty());
            existing.setImageUrl(updatedRecipe.getImageUrl());

            // Kategorie-Update
            if (updatedRecipe.getCategory() != null) {
                try {
                    setCategory(updatedRecipe);
                    existing.setCategory(updatedRecipe.getCategory());
                } catch (IllegalStateException e) {
                    throw new IllegalStateException("Fehler beim Aktualisieren der Kategorie: " + e.getMessage());
                }
            }

            return recipeRepo.save(existing);
        });
    }


    /**
     * Geschäftslogik: Zentraler Endpunkt für Suchen, Filtern und alle Rezepte.
     * (Logik aus @GetMapping des Controllers verschoben).
     */
    public List<Recipe> findAllRecipes(Optional<String> q,
                                       Optional<String> category,
                                       Optional<String> ingredient) {

        if (q.isPresent()) return recipeRepo.findByNameContainingIgnoreCase(q.get());
        if (category.isPresent()) return recipeRepo.findByCategoryNameIgnoreCase(category.get());
        if (ingredient.isPresent()) {
            return recipeRepo.findByIngredientsContainingIgnoreCase(ingredient.get());
        }
        return recipeRepo.findAll();
    }

    public Optional<Recipe> findRecipeById(Long id) {
        return recipeRepo.findById(id);
    }

    public boolean deleteRecipe(Long id) {
        if (!recipeRepo.existsById(id)) return false;
        recipeRepo.deleteById(id);
        return true;
    }

    private void setCategory(Recipe r) {
        if (r.getCategory() == null) {
            throw new IllegalStateException("Kategorie darf nicht null sein.");
        }

        String categoryName = r.getCategory().getName();  // Hier 'r' statt 'recipe'

        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalStateException("Kategorie-Name fehlt im Request.");
        }

        Category existingCategory = categoryRepo.findByName(categoryName);
        if (existingCategory != null) {
            r.setCategory(existingCategory);  // Hier 'r' statt 'recipe'
        } else {
            throw new IllegalStateException("Die Kategorie '" + categoryName +
                    "' existiert nicht in der Datenbank. Nur vordefinierte Kategorien sind erlaubt.");
        }
    }
}


