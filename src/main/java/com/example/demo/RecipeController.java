package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/recipes")
@CrossOrigin(origins = {"http://localhost:5173",
        "https://prog3-frontend-fhkb.onrender.com"})
public class RecipeController {

    private final RecipeService recipeService;

    // Konstruktor verwendet nur den Service
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    //  Alle Rezepte anzeigen, Suchen, Filtern
    @GetMapping
    public List<Recipe> all(@RequestParam Optional<String> q,
                            @RequestParam Optional<String> category,
                            @RequestParam Optional<String> ingredient) {
        // Logik an den Service delegieren
        return recipeService.findAllRecipes(q, category, ingredient);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getOne(@PathVariable Long id) {
        return recipeService.findRecipeById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Rezept erstellen
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Recipe createRecipe(@RequestBody Recipe recipe) {
        return recipeService.saveRecipe(recipe);
    }

    //  Rezept bearbeiten
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> update(@PathVariable Long id, @RequestBody Recipe updatedRecipe) {
        // Logik an den Service delegieren
        return recipeService.updateRecipe(id, updatedRecipe).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Rezept löschen
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (recipeService.deleteRecipe(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
