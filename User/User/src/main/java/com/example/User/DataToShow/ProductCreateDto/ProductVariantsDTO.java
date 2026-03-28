package com.example.User.DataToShow.ProductCreateDto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantsDTO {
    private String attribute;
    private Double price;
    private Integer quantity;

}