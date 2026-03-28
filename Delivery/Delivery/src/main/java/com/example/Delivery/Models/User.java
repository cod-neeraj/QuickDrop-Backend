package com.example.Delivery.Models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;

import org.hibernate.validator.constraints.pl.NIP;

@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "delivery partner",schema = "delivery")
public class User {
    @Id
    private String user_id;
    private String name;
    private String pic;
    private String phoneNumber;
    private String password;
    private String gender;
    private LocalDate dob;
    private String aadharCardNumber;
    private String aadharCardurl;
    private String panCardNumber;
    private String panCardurl;
    private String homeAddress;
    private String vehicleType;
    private String vehicleNumber;
    private String licenseNumber;

    private String role;

    private Boolean isVerified;


    private LocalDate joiningDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Orders> orders;

}
