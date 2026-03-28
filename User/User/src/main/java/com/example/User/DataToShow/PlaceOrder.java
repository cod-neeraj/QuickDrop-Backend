package com.example.User.DataToShow;

import lombok.*;

import java.util.Set;

@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceOrder {
    private String address;
    private Double amount;
    private Double longitude;
    private Double latitude;
    private Set<ProductIds> productIdsSet;
}
