package org.example.ingredientspring.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DishIngredient {
    private Integer id;

    private Dish dish;

    private Ingredient ingredient;

    private Double quantity;
    
    private UnitTypeEnum unitType;
}
