package com.example.User.RequestBodies.Frontend;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class PersonalInfo {
    private String name;
    private String phoneNumber;
    private String emailId;
    private String dob;
    private String gender;
    private String role;
}
