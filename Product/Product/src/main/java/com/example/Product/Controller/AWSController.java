package com.example.Product.Controller;

import com.example.Product.DTO.FileDetailsDTO;
import com.example.Product.DTO.UploadResponse;
import com.example.Product.Service.S3Service;
import com.netflix.discovery.converters.Auto;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/image")
public class AWSController {

    @Autowired
    S3Service s3Service;

    @PostMapping("/upload-url")
    public ResponseEntity<UploadResponse> getUploadUrl(
            @RequestBody FileDetailsDTO fileDetailsDTO) {
        UploadResponse response = s3Service.generateUploadUrl(fileDetailsDTO);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/seller-personal-image-upload-url")
    public ResponseEntity<List<UploadResponse>> getSellerImageUploadUrl(
            @RequestBody List<FileDetailsDTO> fileDetailsDTO) {
        List<UploadResponse> response = s3Service.generateSellerImageUploadUrl(fileDetailsDTO);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/product-image-upload-url")
    public ResponseEntity<UploadResponse> getProductImageUploadUrl(
            @RequestBody FileDetailsDTO fileDetailsDTO) {
        UploadResponse response = s3Service.generateProductUploadUrl(fileDetailsDTO);
        return ResponseEntity.ok(response);

    }
}
