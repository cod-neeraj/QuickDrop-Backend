package com.example.User.FeignInterface;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BestProductDTO {
    List<String> geohashes;
}
