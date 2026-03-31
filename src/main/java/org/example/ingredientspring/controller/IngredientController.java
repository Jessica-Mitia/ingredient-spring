package org.example.ingredientspring.controller;

import org.example.ingredientspring.dto.StockMovementRequest;
import org.example.ingredientspring.entity.Ingredient;
import org.example.ingredientspring.entity.StockMovement;
import org.example.ingredientspring.entity.StockValue;
import org.example.ingredientspring.entity.UnitTypeEnum;
import org.example.ingredientspring.service.IngredientService;
import org.example.ingredientspring.validator.StockRequestValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;
    private final StockRequestValidator stockRequestValidator;

    public IngredientController(IngredientService ingredientService, StockRequestValidator stockRequestValidator) {
        this.ingredientService = ingredientService;
        this.stockRequestValidator = stockRequestValidator;
    }

    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        return ResponseEntity.ok(ingredientService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Integer id) {
        return ResponseEntity.ok(ingredientService.findById(id));
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<StockValue> getStockAt(@PathVariable Integer id,
                                 @RequestParam(required = false) String at,
                                 @RequestParam(required = false) String unit) {
        stockRequestValidator.validateStockParameters(at, unit);
        
        Instant atInstant = Instant.parse(at);
        UnitTypeEnum unitEnum = UnitTypeEnum.valueOf(unit.toUpperCase());
        
        return ResponseEntity.ok(ingredientService.getStockAt(id, atInstant, unitEnum));
    }

    @GetMapping("/{id}/stockMovements")
    public ResponseEntity<List<StockMovement>> getStockMovements(
            @PathVariable Integer id,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        
        Instant fromInstant = from != null ? Instant.parse(from) : null;
        Instant toInstant = to != null ? Instant.parse(to) : null;
        
        return ResponseEntity.ok(ingredientService.getStockMovements(id, fromInstant, toInstant));
    }

    @PostMapping("/{id}/stockMovements")
    public ResponseEntity<List<StockMovement>> addStockMovements(
            @PathVariable Integer id,
            @RequestBody List<StockMovementRequest> requests) {
        
        return ResponseEntity.ok(ingredientService.addStockMovements(id, requests));
    }

}
