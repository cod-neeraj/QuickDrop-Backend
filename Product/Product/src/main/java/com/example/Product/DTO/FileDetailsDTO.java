package com.example.Product.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDetailsDTO {
    private String fileName;
    private String fileType;

}
