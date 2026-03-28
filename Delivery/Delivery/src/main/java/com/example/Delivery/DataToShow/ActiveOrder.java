package com.example.Delivery.DataToShow;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActiveOrder {

    private String orderId;
    private String userName;
    private String phoneNumber;
    private String dropLocation;
    private String status;
}
