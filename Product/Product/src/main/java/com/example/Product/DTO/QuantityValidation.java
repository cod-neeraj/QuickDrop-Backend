package com.example.Product.DTO;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuantityValidation {
    private Integer quantity;
    private String shopkeeperId;
    private Double price;
}
