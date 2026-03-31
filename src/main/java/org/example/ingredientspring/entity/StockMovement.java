package org.example.ingredientspring.entity;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {
    private Integer id;

    private StockValue value;
    
    private MovementTypeEnum type;
    
    private Instant creationDateTime;
}
