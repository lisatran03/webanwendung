package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
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

            // Update der Kategorie-Beziehung
            if (updatedRecipe.getCategory() != null) {
                setCategory(updatedRecipe); // Logik, um Category zu finden/erstellen
                existing.setCategory(updatedRecipe.getCategory());
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

    // Private Methode zur Kapselung der Category-Logik (ehemals setCategoryIfExists)
    private void setCategory(Recipe r) {
        if (r.getCategory() == null) return;

        // Versuch, über ID zu finden
        Long cid = r.getCategory().getId();
        if (cid != null) {
            categoryRepo.findById(cid).ifPresent(r::setCategory);
            return;
        }

        // Versuch, über Namen zu finden oder neu zu erstellen (wenn ID fehlt)
        String name = r.getCategory().getName();
        if (name != null) {
            Category cat = categoryRepo.findByName(name);
            if (cat != null) r.setCategory(cat);
                // Wenn Kategorie nicht existiert, erstelle und speichere sie
            else r.setCategory(categoryRepo.save(new Category(name)));
        }
    }
}

