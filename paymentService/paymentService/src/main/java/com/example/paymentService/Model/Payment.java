package com.example.paymentService.Model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "payment_data")
public class Payment {
    @Id
    private String id;

    @Column(name = "razorpay_id", nullable = false)
    private String razorpayId;
    private String orderId;
    private String phoneNumber;// order ID returned by Razorpay

    private String entity;

    private Integer amount;       // total amount in paise
    private Integer amountPaid;   // amount_paid
    private Integer amountDue;    // amount_due

    private String currency;
    private String receipt;
    private String status;

    private Integer attempts;
    private Date expiryAt;
}
