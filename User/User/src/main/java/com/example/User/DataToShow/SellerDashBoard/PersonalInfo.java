package com.example.User.DataToShow.SellerDashBoard;

import lombok.*;

import java.util.Set;

@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalInfo {

    private String sellerId;
    private String phoneNumber;
    private String name;
    private Set<String> imageUrls;

    private String bio;
    private String shopName;
    private String gstumber;
    private String timings;


    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;

    private Boolean status;
}
