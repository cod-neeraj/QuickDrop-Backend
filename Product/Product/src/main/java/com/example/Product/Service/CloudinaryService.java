package com.example.Product.Service;

import com.cloudinary.Cloudinary;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public class CloudinaryService implements CloudinaryImageService{
    @Autowired
    public Cloudinary cloudinary;

    @Override
    public Map upload(MultipartFile multipartFile) {
        try {
            Map data = this.cloudinary.uploader().upload(multipartFile.getBytes(),Map.of());
            return data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
