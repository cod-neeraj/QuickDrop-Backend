package com.example.User.DTO;

import lombok.*;

import java.util.Date;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private String razorpayId;
    private String orderId;
    private String phoneNumber;
    private String entity;
    private Integer amount;
    private Integer amount_paid;
    private Integer amount_due;
    private String currency;
    private String receipt;
    private String status;
    private Integer attempts;
    private Date expiryAt;
}
