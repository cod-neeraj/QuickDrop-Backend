package com.example.Delivery.DTO;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    private Double longitude;
    private Double latitude;
    private String status;

}
