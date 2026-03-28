package com.example.User.DTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscountDTO {
    private String imageUrl;
    private String heading;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
