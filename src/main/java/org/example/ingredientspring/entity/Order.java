package org.example.ingredientspring.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Integer id;

    private String reference;

    private Instant creationDateTime;

    private List<DishOrder> dishOrders;
}
