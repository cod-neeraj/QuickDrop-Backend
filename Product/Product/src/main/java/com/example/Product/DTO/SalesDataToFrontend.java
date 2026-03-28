package com.example.Product.DTO;

import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesDataToFrontend {

    private String imageUrl;
    private String shopName;
    private String shopAddress;
    private String heading;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
