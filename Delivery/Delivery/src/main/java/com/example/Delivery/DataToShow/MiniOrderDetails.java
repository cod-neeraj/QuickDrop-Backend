package com.example.Delivery.DataToShow;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MiniOrderDetails {
    private String orderId;
    private String droplocation;
    private String orderStatus;
    private Double orderEarnings;
}
