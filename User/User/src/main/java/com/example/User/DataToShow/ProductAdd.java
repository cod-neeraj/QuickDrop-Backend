package com.example.User.DataToShow;

import com.example.User.DTO.PaymentDTO.ProductSave;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductAdd {
    private String productId;
    private String shopkeeperId;
    private String name;
    private String type;
    private String brand;
    private String color;
    private String attribute;
    private String defaultImage;
    private Double price;
    private Long quantity;
    private SellerMiniDetails seller;
}
