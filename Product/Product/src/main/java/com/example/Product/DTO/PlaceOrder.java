package com.example.Product.DTO;

import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaceOrder {
    private String userName;
    private String uerId;
    private Map<Long, List<ProductDetailsForRedis>> productDetails;
}
