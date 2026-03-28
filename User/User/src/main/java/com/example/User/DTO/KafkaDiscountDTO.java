package com.example.User.DTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaDiscountDTO {
    private String sellerId;
    private String imageUrl;
    private String heading;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String name;
    private String shopName;
    private String address;
    private String geohash;
}
