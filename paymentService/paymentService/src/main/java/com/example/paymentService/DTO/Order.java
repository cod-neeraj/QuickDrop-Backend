package com.example.paymentService.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    private String orderId;
    private String userId;
    private String username;

    private String paymentId;
    private String paymentStatus;
    private String orderStatus;
    private Double amount;
    private LocalDate orderDate;
    private LocalDate deliverDate;

    private Map<SellerMiniDetails, List<ProductDetailsForRedis>> productDetails;
}
