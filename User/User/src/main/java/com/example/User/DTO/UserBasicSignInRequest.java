package com.example.User.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBasicSignInRequest {
    @NotNull
    private String name;
    @Email
    private String email;
    @NotNull
    private String password;

}
