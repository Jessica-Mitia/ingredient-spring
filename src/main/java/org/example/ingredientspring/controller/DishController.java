package org.example.ingredientspring.controller;

import org.example.ingredientspring.dto.DishResponse;
import org.example.ingredientspring.entity.Ingredient;
import org.example.ingredientspring.exception.BadRequestException;
import org.example.ingredientspring.service.DishService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public ResponseEntity<List<DishResponse>> getAllDishes() {
        return ResponseEntity.ok(dishService.findAll().stream()
                .map(DishResponse::new)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DishResponse> getDishById(@PathVariable Integer id) {
        return ResponseEntity.ok(new DishResponse(dishService.findById(id)));
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<List<Ingredient>> updateDishIngredients(@PathVariable Integer id, @RequestBody(required = false) List<Ingredient> ingredients) {
        if (ingredients == null) {
            throw new BadRequestException("Le corps de la requête ne peut pas être vide.");
        }
        return ResponseEntity.ok(dishService.updateDishIngredients(id, ingredients));
    }
}
