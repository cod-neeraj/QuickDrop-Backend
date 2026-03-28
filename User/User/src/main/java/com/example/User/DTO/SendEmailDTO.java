package com.example.User.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendEmailDTO {
    private String otp;
    private String email;
}
