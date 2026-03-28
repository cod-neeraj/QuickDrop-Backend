package com.example.Product.DTO;

import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductFetchColorDTO {
    private Set<String> imageUrl;
    private List<String> color;
}

