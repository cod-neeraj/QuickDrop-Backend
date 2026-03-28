package com.example.Delivery.DTO;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopLocation {
    private Double longitude;
    private Double latitude;
}
