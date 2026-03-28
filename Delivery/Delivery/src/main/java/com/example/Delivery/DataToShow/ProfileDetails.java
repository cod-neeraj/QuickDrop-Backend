package com.example.Delivery.DataToShow;

import com.example.Delivery.Models.Orders;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDetails {

    private String name;
    private String pic;
    private String phoneNumber;
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
    private Boolean isVerified;
    private LocalDate joiningDate;

}
