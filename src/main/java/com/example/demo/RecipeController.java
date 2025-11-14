package com.example.demo;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:5173",
        "https://prog3-frontend-fhkb.onrender.com"})
public class RecipeController {

    private List<Recipe> recipes = List.of();

    @GetMapping("/recipes")
    public List<Recipe> getRecipes() {
        return recipes;

    }
}
