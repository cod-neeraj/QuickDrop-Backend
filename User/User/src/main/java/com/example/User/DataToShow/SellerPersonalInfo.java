package com.example.User.DataToShow;

import lombok.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerPersonalInfo {
    private String shopName;
    private String name;
    private String phoneNumber;
    private String gstNumber;
    private String timings;
    private String street;
    private String city;
    private String country;
    private String state;
    private String zipCode;
    private String bio;
    private Set<String> imageUrls;


}
