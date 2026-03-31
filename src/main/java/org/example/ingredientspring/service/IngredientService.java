package org.example.ingredientspring.service;

import org.example.ingredientspring.dto.StockMovementRequest;
import org.example.ingredientspring.entity.*;
import org.example.ingredientspring.exception.ResourceNotFoundException;
import org.example.ingredientspring.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<Ingredient> findAll() {
        return ingredientRepository.findAll();
    }

    public Ingredient findById(Integer id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient.id=" + id + " is not found"));
    }

    public StockValue getStockAt(Integer ingredientId, Instant at, UnitTypeEnum unit) {
        Ingredient ingredient = findById(ingredientId);

        double totalStock = 0;
        if (ingredient.getStockMovementList() != null) {
            for (StockMovement movement : ingredient.getStockMovementList()) {
                if (movement.getCreationDateTime() == null || !movement.getCreationDateTime().isAfter(at)) {
                    if (movement.getValue() != null && movement.getValue().getUnit() == unit) {
                        if (movement.getType() == MovementTypeEnum.IN) {
                            totalStock += movement.getValue().getQuantity();
                        } else if (movement.getType() == MovementTypeEnum.OUT) {
                            totalStock -= movement.getValue().getQuantity();
                        }
                    }
                }
            }
        }

        return new StockValue(totalStock, unit);
    }

    public List<StockMovement> getStockMovements(Integer ingredientId, Instant from, Instant to) {
        Ingredient ingredient = findById(ingredientId);
        
        return ingredient.getStockMovementList().stream()
                .filter(m -> {
                    Instant creation = m.getCreationDateTime();
                    if (creation == null) return false;
                    return (from == null || !creation.isBefore(from)) && (to == null || !creation.isAfter(to));
                })
                .collect(Collectors.toList());
    }

    public List<StockMovement> addStockMovements(Integer ingredientId, List<StockMovementRequest> requests) {
        Ingredient ingredient = findById(ingredientId);

        List<StockMovement> newMovements = requests.stream()
                .map(req -> {
                    StockMovement m = new StockMovement();
                    m.setValue(new StockValue(req.getValue(), req.getUnit()));
                    m.setType(req.getType());
                    m.setCreationDateTime(Instant.now());
                    return m;
                })
                .collect(Collectors.toList());

        if (ingredient.getStockMovementList() == null) {
            ingredient.setStockMovementList(new ArrayList<>());
        }
        
        ingredient.getStockMovementList().addAll(newMovements);
        ingredientRepository.save(ingredient);
        
        return newMovements;
    }
}
