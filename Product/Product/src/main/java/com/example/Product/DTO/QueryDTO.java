package com.example.Product.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryDTO {
    private String category;
    private String[] brands;
    private String[] colors;
    private Double price;
    private String type;
}
