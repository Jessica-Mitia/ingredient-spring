package org.example.ingredientspring.service;

import org.example.ingredientspring.entity.Dish;
import org.example.ingredientspring.entity.DishIngredient;
import org.example.ingredientspring.entity.Ingredient;
import org.example.ingredientspring.exception.ResourceNotFoundException;
import org.example.ingredientspring.repository.DishRepository;
import org.example.ingredientspring.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DishService {

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    public Dish findById(Integer id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish.id=" + id + " is not found"));
    }

    @Transactional
    public List<Ingredient> updateDishIngredients(Integer dishId, List<Ingredient> ingredientsPayload) {
        Dish dish = findById(dishId);

        List<DishIngredient> currentAssociations = dish.getDishIngredients();
        if (currentAssociations == null) {
            currentAssociations = new ArrayList<>();
            dish.setDishIngredients(currentAssociations);
        }

        List<Integer> requestedIds = ingredientsPayload.stream()
                .map(Ingredient::getId)
                .filter(id -> id != null)
                .collect(Collectors.toList());

        currentAssociations.removeIf(di -> di.getIngredient() != null && !requestedIds.contains(di.getIngredient().getId()));

        List<Integer> existingIds = currentAssociations.stream()
                .map(di -> di.getIngredient().getId())
                .collect(Collectors.toList());

        for (Integer reqId : requestedIds) {
            if (!existingIds.contains(reqId)) {
                Optional<Ingredient> optionalIngredient = ingredientRepository.findById(reqId);
                if (optionalIngredient.isPresent()) {
                    DishIngredient newAssoc = new DishIngredient();
                    newAssoc.setDish(dish);
                    newAssoc.setIngredient(optionalIngredient.get());
                    currentAssociations.add(newAssoc);
                }
            }
        }

        dishRepository.save(dish);

        return currentAssociations.stream()
                .map(DishIngredient::getIngredient)
                .collect(Collectors.toList());
    }
}
