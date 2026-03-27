package org.example.ingredientspring.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class StockValue {
    private double quantity;
    
    @Enumerated(EnumType.STRING)
    private UnitTypeEnum unit;
}
