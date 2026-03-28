package com.example.Product.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductId {
    private Long productSizeId;
    private Integer quantity;
}
