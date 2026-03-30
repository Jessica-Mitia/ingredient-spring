package org.example.ingredientspring.controller;

import org.example.ingredientspring.entity.Ingredient;
import org.example.ingredientspring.entity.StockValue;
import org.example.ingredientspring.entity.UnitTypeEnum;
import org.example.ingredientspring.service.IngredientService;
import org.example.ingredientspring.validator.StockRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private StockRequestValidator stockRequestValidator;

    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return ingredientService.findAll();
    }

    @GetMapping("/{id}")
    public Ingredient getIngredientById(@PathVariable Integer id) {
        return ingredientService.findById(id);
    }

    @GetMapping("/{id}/stock")
    public StockValue getStockAt(@PathVariable Integer id,
                                 @RequestParam(required = false) String at,
                                 @RequestParam(required = false) String unit) {
        stockRequestValidator.validateStockParameters(at, unit);
        
        Instant atInstant = Instant.parse(at);
        UnitTypeEnum unitEnum = UnitTypeEnum.valueOf(unit.toUpperCase());
        
        return ingredientService.getStockAt(id, atInstant, unitEnum);
    }
}
