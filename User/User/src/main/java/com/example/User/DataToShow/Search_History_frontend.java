package com.example.User.DataToShow;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Search_History_frontend {
    private String query;
    private LocalDate date;
}
