package org.example.ingredientspring.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;
    private Double price;
    
    @Enumerated(EnumType.STRING)
    private CategoryEnum category;
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_ingredient")
    private List<StockMovement> stockMovementList;
}
