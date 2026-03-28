package com.example.User.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.boot.web.servlet.ServletRegistration;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginUserRequest {
    @NotNull
    private String email;

    @NotNull
    private String password;
}
