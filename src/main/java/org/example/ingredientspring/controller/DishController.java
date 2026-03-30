package org.example.ingredientspring.controller;

import org.example.ingredientspring.dto.DishResponse;
import org.example.ingredientspring.entity.Ingredient;
import org.example.ingredientspring.exception.BadRequestException;
import org.example.ingredientspring.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dishes")
public class DishController {

    @Autowired
    private DishService dishService;

    @GetMapping
    public List<DishResponse> getAllDishes() {
        return dishService.findAll().stream()
                .map(DishResponse::new)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}/ingredients")
    public List<Ingredient> updateDishIngredients(@PathVariable Integer id, @RequestBody(required = false) List<Ingredient> ingredients) {
        if (ingredients == null) {
            throw new BadRequestException("Le corps de la requête ne peut pas être vide.");
        }
        return dishService.updateDishIngredients(id, ingredients);
    }
}
