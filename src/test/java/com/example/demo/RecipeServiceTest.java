package com.example.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    RecipeRepository recipeRepo;

    @Mock
    CategoryRepository categoryRepo;

    @InjectMocks
    RecipeService service;

    // ---------- Helpers ----------
    private static Category cat(String name) {
        Category c = new Category();
        c.setName(name);
        return c;
    }

    private static Recipe recipeWithCategory(String name, String categoryName) {
        Recipe r = new Recipe();
        r.setName(name);
        r.setCategory(cat(categoryName));
        r.setIngredients("Zutat1\nZutat2");
        r.setInstructions("Step1\nStep2");
        r.setTime(20);
        r.setDifficulty("easy");
        r.setImageUrl("https://example.com/img.jpg");
        return r;
    }

    // 1) saveRecipe OK
    @Test
    void saveRecipe_saves_whenCategoryExists() {
        Recipe input = recipeWithCategory("Pasta", "Hauptgerichte");
        Category existing = cat("Hauptgerichte");

        when(categoryRepo.findByName("Hauptgerichte")).thenReturn(existing);
        when(recipeRepo.save(any(Recipe.class))).thenAnswer(inv -> inv.getArgument(0));

        Recipe saved = service.saveRecipe(input);

        assertNotNull(saved);
        assertEquals("Pasta", saved.getName());
        assertNotNull(saved.getCategory());
        assertEquals("Hauptgerichte", saved.getCategory().getName());

        verify(categoryRepo).findByName("Hauptgerichte");
        verify(recipeRepo).save(any(Recipe.class));
    }

    // 2) saveRecipe Kategorie fehlt
    @Test
    void saveRecipe_throws_whenCategoryNull() {
        Recipe r = new Recipe();
        r.setName("Test");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.saveRecipe(r));
        assertTrue(ex.getMessage().toLowerCase().contains("kategorie"));

        verifyNoInteractions(categoryRepo);
        verifyNoInteractions(recipeRepo);
    }

    // 3) saveRecipe Kategorie existiert nicht
    @Test
    void saveRecipe_throws_whenCategoryNotFound() {
        Recipe r = recipeWithCategory("Curry", "Vegetarisch");
        when(categoryRepo.findByName("Vegetarisch")).thenReturn(null);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.saveRecipe(r));
        assertTrue(ex.getMessage().toLowerCase().contains("existiert nicht"));

        verify(categoryRepo).findByName("Vegetarisch");
        verify(recipeRepo, never()).save(any());
    }

    // 4) updateRecipe OK (inkl. Kategorie-Update)
    @Test
    void updateRecipe_updatesFields_andCategory_whenFound() {
        Recipe existing = recipeWithCategory("Alt", "Vorspeisen");
        existing.setId(1L);

        Recipe updated = recipeWithCategory("Neu", "Hauptgerichte");
        updated.setTime(45);
        updated.setDifficulty("hard");
        updated.setImageUrl("https://example.com/new.jpg");

        Category catNew = cat("Hauptgerichte");

        when(recipeRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepo.findByName("Hauptgerichte")).thenReturn(catNew);
        when(recipeRepo.save(any(Recipe.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Recipe> result = service.updateRecipe(1L, updated);

        assertTrue(result.isPresent());
        Recipe saved = result.get();

        assertEquals("Neu", saved.getName());
        assertEquals("Hauptgerichte", saved.getCategory().getName());
        assertEquals(45, saved.getTime());
        assertEquals("hard", saved.getDifficulty());
        assertEquals("https://example.com/new.jpg", saved.getImageUrl());

        verify(recipeRepo).findById(1L);
        verify(categoryRepo).findByName("Hauptgerichte");
        verify(recipeRepo).save(any(Recipe.class));
    }

    // 5) deleteRecipe OK
    @Test
    void deleteRecipe_returnsTrue_andDeletes_whenExists() {
        when(recipeRepo.existsById(5L)).thenReturn(true);

        boolean ok = service.deleteRecipe(5L);

        assertTrue(ok);
        verify(recipeRepo).existsById(5L);
        verify(recipeRepo).deleteById(5L);
    }
}
