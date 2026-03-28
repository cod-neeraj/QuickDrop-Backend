package com.example.Delivery.DataToShow;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasicDashBoard {

    private String deliveryBoyId;
    private String name;
    private String phoneNumber;
    private Double todayEarnings;
    private Integer todayDeliveries;
    private Double thisWeekEarnings;
    private String status;
    private ActiveOrder activeOrder;

}
