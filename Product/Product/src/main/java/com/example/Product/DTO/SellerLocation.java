package com.example.Product.DTO;

import lombok.*;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerLocation {
    private String sellerId;
    private Double longitude;
    private Double latitude;
}
