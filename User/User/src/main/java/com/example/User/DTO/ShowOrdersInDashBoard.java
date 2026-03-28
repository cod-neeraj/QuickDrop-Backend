package com.example.User.DTO;

import com.example.User.Models.PaymentMode;
import com.example.User.Models.PaymentStatus;
import com.example.User.Models.Seller.OrderStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShowOrdersInDashBoard {
    private String orderId;
    private OrderStatus orderStatus;
    private PaymentMode paymentMethod;
    private PaymentStatus paymentStatus;
    private String userAddress;
    private LocalDate orderDate;
    private Double totalAmount;
}
