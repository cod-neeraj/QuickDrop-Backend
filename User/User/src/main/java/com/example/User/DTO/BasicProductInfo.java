package com.example.User.DTO;

import lombok.*;

@Getter
@Setter

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasicProductInfo {
    private String productId;
    private String productName;
    private String type;
    private String brand;
    private String color;
    private String attribute;
    private Double price;
    private Long quantity;
}
