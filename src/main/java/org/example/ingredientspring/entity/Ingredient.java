package org.example.ingredientspring.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient {
    private Integer id;

    private String name;
    private Double price;
    
    private CategoryEnum category;
    
    private List<StockMovement> stockMovementList;
}
