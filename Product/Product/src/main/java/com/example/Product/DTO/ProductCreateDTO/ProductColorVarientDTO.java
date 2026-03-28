package com.example.Product.DTO.ProductCreateDTO;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class ProductColorVarientDTO {
    private String color;
    private Set<String> urls;

    public ProductColorVarientDTO(String color ,Set<String> urls){
        this.color=color;
        this.urls = urls;
    }
}
