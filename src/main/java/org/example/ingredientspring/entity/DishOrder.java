package org.example.ingredientspring.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DishOrder {
    private Integer id;

    private Dish dish;

    private Order order;

    private Integer quantity;
}
