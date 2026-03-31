package org.example.ingredientspring.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockValue {
    private double quantity;
    
    private UnitTypeEnum unit;
}
