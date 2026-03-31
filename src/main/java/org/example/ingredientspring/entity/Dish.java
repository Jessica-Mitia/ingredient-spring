package org.example.ingredientspring.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dish {
    private Integer id;

    private String name;
    
    private DishTypeEnum dishType;
    
    private List<DishIngredient> dishIngredients;
    
    private Double price;
}
