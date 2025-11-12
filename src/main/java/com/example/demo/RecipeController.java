package com.example.demo;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*") // erlaubt alle Ursprünge, praktisch für Frontend-Tests
public class RecipeController {

    private List<Recipe> recipes = List.of(
            new Recipe("Oatmeal", "Breakfast", "Oats, Milk, Honey", "Cook oats with milk and add honey."),
            new Recipe("Grilled Cheese Sandwich", "Lunch", "Bread, Cheese, Butter", "Butter bread, add cheese, and grill."),
            new Recipe("Spaghetti Bolognese", "Dinner", "Spaghetti, Ground Beef, Tomato Sauce", "Cook spaghetti and prepare sauce with ground beef.")
    );

    @GetMapping("/recipes")
    public List<Recipe> getAllRecipes() {
        return recipes;

    }
}
