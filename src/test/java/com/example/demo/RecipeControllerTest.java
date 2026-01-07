package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RecipeService recipeService;

    private static Category cat(String name) {
        Category c = new Category();
        c.setName(name);
        return c;
    }

    private static Recipe recipe(long id, String name, String categoryName) {
        Recipe r = new Recipe();
        r.setId(id);
        r.setName(name);
        r.setCategory(cat(categoryName));
        r.setIngredients("Zutat1\nZutat2");
        r.setInstructions("Step1\nStep2");
        r.setTime(30);
        r.setDifficulty("easy");
        r.setImageUrl("https://example.com/img.jpg");
        return r;
    }

    // 1) GET /recipes
    @Test
    void getAllRecipes_returns200_andArray() throws Exception {
        when(recipeService.findAllRecipes(Optional.empty(), Optional.empty(), Optional.empty()))
                .thenReturn(List.of(recipe(1, "Pasta", "Hauptgerichte")));

        mvc.perform(get("/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Pasta"))
                .andExpect(jsonPath("$[0].category.name").value("Hauptgerichte"));
    }

    // 2) GET /recipes?q=...
    @Test
    void getAllRecipes_withQueryParam_callsServiceWithQ() throws Exception {
        when(recipeService.findAllRecipes(Optional.of("pas"), Optional.empty(), Optional.empty()))
                .thenReturn(List.of(recipe(1, "Pasta", "Hauptgerichte")));

        mvc.perform(get("/recipes").param("q", "pas"))
                .andExpect(status().isOk());

        verify(recipeService).findAllRecipes(Optional.of("pas"), Optional.empty(), Optional.empty());
    }

    // 3) GET /recipes/{id} -> 200
    @Test
    void getRecipeById_whenFound_returns200() throws Exception {
        when(recipeService.findRecipeById(1L)).thenReturn(Optional.of(recipe(1, "Curry", "Vegetarisch")));

        mvc.perform(get("/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Curry"))
                .andExpect(jsonPath("$.category.name").value("Vegetarisch"));
    }

    // 4) GET /recipes/{id} -> 404
    @Test
    void getRecipeById_whenMissing_returns404() throws Exception {
        when(recipeService.findRecipeById(999L)).thenReturn(Optional.empty());

        mvc.perform(get("/recipes/999"))
                .andExpect(status().isNotFound());
    }

    // 5) POST /recipes -> 201
    @Test
    void createRecipe_returns201() throws Exception {
        Recipe input = recipe(0, "Neu", "Hauptgerichte");
        input.setId(null);
        Recipe saved = recipe(10, "Neu", "Hauptgerichte");

        when(recipeService.saveRecipe(ArgumentMatchers.any(Recipe.class))).thenReturn(saved);

        mvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Neu"))
                .andExpect(jsonPath("$.category.name").value("Hauptgerichte"));
    }

    // 6) DELETE /recipes/{id} -> 204 (exists)
    @Test
    void deleteRecipe_whenExists_returns204() throws Exception {
        when(recipeService.deleteRecipe(5L)).thenReturn(true);

        mvc.perform(delete("/recipes/5"))
                .andExpect(status().isNoContent());

        verify(recipeService).deleteRecipe(5L);
    }
}
