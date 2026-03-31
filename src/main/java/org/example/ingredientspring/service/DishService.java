package org.example.ingredientspring.service;

import org.example.ingredientspring.entity.Dish;
import org.example.ingredientspring.entity.DishIngredient;
import org.example.ingredientspring.entity.Ingredient;
import org.example.ingredientspring.exception.ResourceNotFoundException;
import org.example.ingredientspring.repository.DishIngredientRepository;
import org.example.ingredientspring.repository.DishRepository;
import org.example.ingredientspring.repository.IngredientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DishService {

    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;
    private final DishIngredientRepository dishIngredientRepository;

    public DishService(DishRepository dishRepository, 
                       IngredientRepository ingredientRepository, 
                       DishIngredientRepository dishIngredientRepository) {
        this.dishRepository = dishRepository;
        this.ingredientRepository = ingredientRepository;
        this.dishIngredientRepository = dishIngredientRepository;
    }

    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    public Dish findById(Integer id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish.id=" + id + " is not found"));

        List<DishIngredient> associations = dishIngredientRepository.findByDishId(id);
        for (DishIngredient di : associations) {
            Optional<Ingredient> optionalIngredient = ingredientRepository.findById(di.getIngredient().getId());
            if (optionalIngredient.isPresent()) {
                di.setIngredient(optionalIngredient.get());
            }
            Optional<Dish> optionalDish = dishRepository.findById(di.getDish().getId());
            if (optionalDish.isPresent()) {
                di.setDish(optionalDish.get());
            }
        }
        dish.setDishIngredients(associations);
        return dish;
    }

    @Transactional
    public List<Ingredient> updateDishIngredients(Integer dishId, List<Ingredient> ingredientsPayload) {
        Dish dish = findById(dishId);

        dishIngredientRepository.deleteByDishId(dishId);

        List<DishIngredient> newAssociations = new ArrayList<>();
        for (Ingredient ingredientReq : ingredientsPayload) {
            if (ingredientReq.getId() != null) {
                Optional<Ingredient> optionalIngredient = ingredientRepository.findById(ingredientReq.getId());
                if (optionalIngredient.isPresent()) {
                    DishIngredient newAssoc = new DishIngredient();
                    newAssoc.setDish(dish);
                    newAssoc.setIngredient(optionalIngredient.get());
                    newAssoc.setQuantity(1.0); 
                    
                    dishIngredientRepository.save(newAssoc, dishId);
                    newAssociations.add(newAssoc);
                }
            }
        }

        dish.setDishIngredients(newAssociations);
        return newAssociations.stream()
                .map(DishIngredient::getIngredient)
                .collect(Collectors.toList());
    }
}
