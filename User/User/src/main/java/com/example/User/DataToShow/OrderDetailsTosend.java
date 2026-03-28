package com.example.User.DataToShow;

import com.example.User.RequestBodies.Frontend.CreateCustomerAddress;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailsTosend {
    private String order_Id;
    private OrderDetailsInRedis orderDetailsInRedis;
}
