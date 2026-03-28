package com.example.Product.DTO;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageDTO {

    private Set<String> productImageUrls;
}
