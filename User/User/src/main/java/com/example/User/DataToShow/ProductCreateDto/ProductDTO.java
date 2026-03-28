package com.example.User.DataToShow.ProductCreateDto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {

    private String name;
    private String description;
    private String type;
    private String brand;
    private String productCategory;
    private String defaultImageUrl;
    private List<ProductVariantsDTO> productVariantsDTOList;
    private List<ProductColorVariant> colors;



}