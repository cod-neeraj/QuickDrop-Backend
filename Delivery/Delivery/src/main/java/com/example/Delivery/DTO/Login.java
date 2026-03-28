package com.example.Delivery.DTO;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class Login {
    private String phoneNumber;
    private String password;
}
