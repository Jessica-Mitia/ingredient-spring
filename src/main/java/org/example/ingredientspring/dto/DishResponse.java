package org.example.ingredientspring.dto;

import org.example.ingredientspring.entity.Dish;
import org.example.ingredientspring.entity.DishIngredient;
import org.example.ingredientspring.entity.Ingredient;

import java.util.List;
import java.util.stream.Collectors;

public class DishResponse {
    private Integer id;
    private String name;
    private Double price;
    private List<Ingredient> ingredients;

    public DishResponse(Dish dish) {
        this.id = dish.getId();
        this.name = dish.getName();
        this.price = dish.getPrice();
        if (dish.getDishIngredients() != null) {
            this.ingredients = dish.getDishIngredients().stream()
                    .map(DishIngredient::getIngredient)
                    .collect(Collectors.toList());
        }
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public List<Ingredient> getIngredients() { return ingredients; }
}
