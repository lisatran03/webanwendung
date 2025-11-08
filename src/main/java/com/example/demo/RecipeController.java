package com.example.demo;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@CrossOrigin(origins = "https://prog3-frontend-fhkb.onrender.com")
public class RecipeController {

        @GetMapping("/recipes")
        public List<Recipe> getRecipe() {
            return List.of(new Recipe("Oatmeal", "Breakfast", "Oats, Milk, Honey", "Cook oats with milk and add honey."),
                           new Recipe("Grilled Cheese Sandwich", "Lunch", "Bread, Cheese, Butter", "Butter bread, add cheese, and grill."),
                           new Recipe("Spaghetti Bolognese", "Dinner", "Spaghetti, Ground Beef, Tomato Sauce", "Cook spaghetti and prepare sauce with ground beef."));
        }
}
