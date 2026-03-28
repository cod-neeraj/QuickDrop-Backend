package com.example.User.RequestBodies.Frontend;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCustomerAddress {
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private String phoneNumber;
    private Boolean isDefault;
}

