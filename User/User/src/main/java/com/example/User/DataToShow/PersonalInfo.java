package com.example.User.DataToShow;

import com.example.User.Models.Gender;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalInfo {
    private Long id;
    private String name;
    private String emailId;
    private String dob;
    private Gender gender;
}
