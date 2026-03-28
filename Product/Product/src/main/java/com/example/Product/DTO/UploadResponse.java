package com.example.Product.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadResponse {
    private String uploadUrl;
    private String fileKey;
    private String fileUrl;
}
