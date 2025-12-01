package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/recipes")
@CrossOrigin(origins = {"http://localhost:5173",
        "https://prog3-frontend-fhkb.onrender.com"})
public class RecipeController {
    private final RecipeRepository recipeRepo;
    private final CategoryRepository categoryRepo;

    public RecipeController(RecipeRepository recipeRepo, CategoryRepository categoryRepo) {
        this.recipeRepo = recipeRepo;
        this.categoryRepo = categoryRepo;
    }


    @GetMapping
    public List<Recipe> all(@RequestParam Optional<String> q, @RequestParam Optional<String> category) {
        if (q.isPresent()) return recipeRepo.findByNameContainingIgnoreCase(q.get());
        if (category.isPresent()) return recipeRepo.findByCategoryNameIgnoreCase(category.get());
        return recipeRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getOne(@PathVariable Long id) {
        return recipeRepo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Recipe createRecipe(@RequestBody Recipe recipe) {
        return recipeRepo.save(recipe);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recipe> update(@PathVariable Long id, @RequestBody Recipe r) {
        return recipeRepo.findById(id).map(existing -> {
            existing.setName(r.getName());
            existing.setInstructions(r.getInstructions());
            existing.setIngredients(r.getIngredients());
            if (r.getCategory() != null) {
                setCategoryIfExists(r);
                existing.setCategory(r.getCategory());
            }
            recipeRepo.save(existing);
            return ResponseEntity.ok(existing);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!recipeRepo.existsById(id)) return ResponseEntity.notFound().build();
        recipeRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void setCategoryIfExists(Recipe r) {
        if (r.getCategory() == null) return;
        Long cid = r.getCategory().getId();
        if (cid != null) {
            categoryRepo.findById(cid).ifPresent(r::setCategory);
            return;
        }
        String name = r.getCategory().getName();
        if (name != null) {
            Category cat = categoryRepo.findByName(name);
            if (cat != null) r.setCategory(cat);
            else r.setCategory(categoryRepo.save(new Category(name)));
        }
    }

}
