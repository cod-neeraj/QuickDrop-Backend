package com.example.User.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_basic_data", schema = "users")
public class User implements Serializable {
    @Id
    @Column(name = "id", updatable = false, nullable = false,unique = true)
    private String id;

    private String name;
    private String phoneNumber;

    @Email
    private String emailId;
    private String password;

    private String dob;
    private String gender;

    private String role;

}
