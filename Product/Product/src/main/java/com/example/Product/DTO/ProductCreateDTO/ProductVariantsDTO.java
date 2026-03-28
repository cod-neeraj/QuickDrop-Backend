package com.example.Product.DTO.ProductCreateDTO;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantsDTO {
    private String attribute;
    private Double price;
    private Integer quantity;

}
