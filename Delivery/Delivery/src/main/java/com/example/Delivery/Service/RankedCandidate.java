package com.example.Delivery.Service;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RankedCandidate {
    private String deliveryBoyId;
    private double distance;
    private int points;

}
