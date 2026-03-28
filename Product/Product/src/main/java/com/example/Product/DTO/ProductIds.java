package com.example.Product.DTO;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductIds {

    private Long productSizeId;
    private Integer quantity;
    private String shopkeeperId;

}
