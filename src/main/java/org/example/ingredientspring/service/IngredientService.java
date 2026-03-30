package org.example.ingredientspring.service;

import org.example.ingredientspring.entity.Ingredient;
import org.example.ingredientspring.entity.MovementTypeEnum;
import org.example.ingredientspring.entity.StockMovement;
import org.example.ingredientspring.entity.StockValue;
import org.example.ingredientspring.entity.UnitTypeEnum;
import org.example.ingredientspring.exception.ResourceNotFoundException;
import org.example.ingredientspring.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

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
}
