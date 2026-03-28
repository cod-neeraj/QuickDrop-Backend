package com.example.paymentService.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RazorPayOrderDetails {
    private String razorpayId;
    private String entity;
    private Integer amount;
    private Integer amount_paid;
    private Integer amount_due;
    private String currency;
    private String receipt;
    private String status;
    private Integer attempts;
}
