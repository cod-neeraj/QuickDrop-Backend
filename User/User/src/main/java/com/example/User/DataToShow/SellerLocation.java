package com.example.User.DataToShow;

import lombok.*;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerLocation {
    private String sellerId;
    private double latitude;
    private double longitude;
}
