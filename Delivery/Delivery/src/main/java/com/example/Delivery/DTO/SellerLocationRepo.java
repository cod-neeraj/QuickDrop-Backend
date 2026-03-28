package com.example.Delivery.DTO;

import lombok.*;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerLocationRepo {
    private String sellerId;
    private Point location;
}
