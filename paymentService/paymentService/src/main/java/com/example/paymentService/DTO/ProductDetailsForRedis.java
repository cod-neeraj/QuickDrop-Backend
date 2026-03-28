package com.example.paymentService.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDetailsForRedis {
    private String productId;
    private String name;
    private String type;
    private String brand;
    private String color;
    private String defaultImage;
    private String attribute;
    private Double price;
    private Long quantity;
}
