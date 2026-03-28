package com.example.Product.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchProduct {
    private String query;
    private Double minPrice;
    private Double maxPrice;
    private Double distance;
}
