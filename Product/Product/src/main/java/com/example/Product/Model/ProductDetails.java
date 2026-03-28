package com.example.Product.Model;

import lombok.*;
import org.springframework.context.annotation.Bean;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetails {
    private String productId;
    private Integer quantity;
}
