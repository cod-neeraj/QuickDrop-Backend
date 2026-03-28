package com.example.User.RequestBodies.Frontend;

import com.example.User.Models.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUp {
    @NotNull
    private String name;
    @NotNull
    private String phoneNumber;
    @Email
    private String emailId;
    @NotNull
    private String password;
    @NotNull
    private String dob;
    @NotNull
    private Double longitude;
    @NotNull
    private Double latitude;
    @NotNull
    private String gender;
    @NotNull
    private String role;
}
