package com.example.User.DTO;

import com.example.User.Models.Customer.CartProductDetails;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddProductToCart {
    private String sellerId;
    private String name;
    private String shopName;
    private String street;
    private String city;
    private String state;
    private CartProductDetails cartProductDetailsList;

}
