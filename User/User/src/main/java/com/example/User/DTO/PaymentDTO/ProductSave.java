package com.example.User.DTO.PaymentDTO;

import jakarta.validation.constraints.AssertFalse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSave {
    private String productId;
    private String shopkeeperId;
    private String name;
    private String type;
    private String brand;
    private String color;
    private String attribute;
    private Double price;
    private Long quantity;





}
