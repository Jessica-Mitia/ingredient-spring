package org.example.ingredientspring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ingredientspring.entity.MovementTypeEnum;
import org.example.ingredientspring.entity.UnitTypeEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementRequest {
    private UnitTypeEnum unit;
    private Double value;
    private MovementTypeEnum type;
}
