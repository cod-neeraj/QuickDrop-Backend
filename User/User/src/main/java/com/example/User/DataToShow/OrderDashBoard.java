package com.example.User.DataToShow;

import com.example.User.Models.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDashBoard {
    private String orderId;
    private LocalDate orderDate;
    private Double totalAmount;
    private String orderStatus;

}
