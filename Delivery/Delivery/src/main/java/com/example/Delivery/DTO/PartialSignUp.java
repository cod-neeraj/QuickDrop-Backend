package com.example.Delivery.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PartialSignUp {
    private String name;
    private String phoneNumber;
    private String password;
}
