package com.example.Product.Service;

import com.example.Product.DTO.FileDetailsDTO;
import com.example.Product.DTO.UploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class S3Service {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${cdn.url}")
    private String cdnUrl;


    private final S3Presigner s3Presigner;

    public S3Service(S3Presigner s3Presigner) {
        this.s3Presigner = s3Presigner;
    }

    public UploadResponse generateUploadUrl(FileDetailsDTO dto) {

        if (!dto.getFileType().startsWith("image/")) {
            throw new RuntimeException("Only image uploads allowed");
        }

        String cleanFileName = dto.getFileName()
                .replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

        String key = "offers/" + UUID.randomUUID() + "_" + cleanFileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();


        PresignedPutObjectRequest presignedRequest =
                s3Presigner.presignPutObject(p -> p
                        .putObjectRequest(putObjectRequest)
                        .signatureDuration(Duration.ofMinutes(10))
                );

        String uploadUrl = presignedRequest.url().toString();
        String fileUrl = cdnUrl + "/" + key;

        return new UploadResponse(uploadUrl, key, fileUrl);
    }

    public List<UploadResponse> generateSellerImageUploadUrl(List<FileDetailsDTO> dto) {
        List<UploadResponse> list = new ArrayList<>();
        for (int i = 0; i < dto.size(); i++) {

            if (!dto.get(i).getFileType().startsWith("image/")) {
                throw new RuntimeException("Only image uploads allowed");
            }

            String cleanFileName = dto.get(i).getFileName()
                    .replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

            String key = "sellerPersonalImage/" + UUID.randomUUID() + "_" + cleanFileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();


            PresignedPutObjectRequest presignedRequest =
                    s3Presigner.presignPutObject(p -> p
                            .putObjectRequest(putObjectRequest)
                            .signatureDuration(Duration.ofMinutes(10))
                    );

            String uploadUrl = presignedRequest.url().toString();
            String fileUrl = cdnUrl + "/" + key;


            list.add(new UploadResponse(uploadUrl, key, fileUrl));
        }
        return list;
    }

    public UploadResponse generateProductUploadUrl(FileDetailsDTO dto) {

        if (!dto.getFileType().startsWith("image/")) {
            throw new RuntimeException("Only image uploads allowed");
        }

        String cleanFileName = dto.getFileName()
                .replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

        String key = "products/" + UUID.randomUUID() + "_" + cleanFileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();


        PresignedPutObjectRequest presignedRequest =
                s3Presigner.presignPutObject(p -> p
                        .putObjectRequest(putObjectRequest)
                        .signatureDuration(Duration.ofMinutes(10))
                );

        String uploadUrl = presignedRequest.url().toString();
        String fileUrl = cdnUrl + "/" + key;

        return new UploadResponse(uploadUrl, key, fileUrl);
    }

}

