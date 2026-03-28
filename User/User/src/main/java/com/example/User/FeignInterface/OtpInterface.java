package com.example.User.FeignInterface;

import com.example.User.DTO.SendEmailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name="email-service",url="http://localhost:9093")
public interface OtpInterface {

    @PostMapping("/email/forgotPasswordEmail")
    public ResponseEntity<?> sendEmail(@RequestBody SendEmailDTO sendEmailDTO) ;
}
