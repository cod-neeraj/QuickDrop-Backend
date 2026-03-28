package com.example.User.DTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscountDTOKafka {
    private String shopkeeperId;
    private String imageUrl;
    private String heading;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double longitude;
    private Double latitude;
}
