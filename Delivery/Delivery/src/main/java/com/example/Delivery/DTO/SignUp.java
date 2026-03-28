package com.example.Delivery.DTO;

import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUp {

    private String pic;
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
    private Double latitude;
    private Double longitude;
}
