package org.example.ingredientspring.validator;

import org.example.ingredientspring.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class StockRequestValidator {

    public void validateStockParameters(String at, String unit) {
        if (at == null || unit == null) {
            throw new BadRequestException("Either mandatory query parameter `at` or `unit` is not provided.");
        }
    }
}
