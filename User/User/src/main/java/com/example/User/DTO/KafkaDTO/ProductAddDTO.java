package com.example.User.DTO.KafkaDTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ProductAddDTO {
    private String shopkeeperPhoneNumber;

    private String productId;
    private String productName;
    private String productCategory;
}
