package com.example.paymentService.Controller;

import com.example.paymentService.Model.Payment;
import com.example.paymentService.Service.OrderPayment;
import com.example.paymentService.Service.verifyPayment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.razorpay.RazorpayException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PaymentController {


    @Autowired
    private verifyPayment verifyPayment;

    @Autowired
    private OrderPayment orderPayment;

    private static final String SECRET_KEY = "zRdyCfcV3YR3gal5KYVVFwB4";

    @GetMapping("/payment-order/{key}/{amount}/{phoneNumber}")
    public ResponseEntity<Payment> makePayment(@PathVariable String key,
                                               @PathVariable Double amount,
                                               @PathVariable String phoneNumber) throws JsonProcessingException, RazorpayException {

        Payment payment =  orderPayment.makePayment(key,amount,phoneNumber);
        if(payment != null){
            return ResponseEntity.ok(payment);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);


    }

    @PostMapping("/verifyPayment")
    public ResponseEntity<Map<String, String>> verifyPayment(@RequestBody Map<String, String> payload) {
        try {
            String razorpayOrderId = payload.get("razorpay_order_id");
            String razorpayPaymentId = payload.get("razorpay_payment_id");
            String razorpaySignature = payload.get("razorpay_signature");
            String orderId = payload.get("orderId");
            String phoneNumber = payload.get("username");
            System.out.println(orderId+" "+phoneNumber);

            String data = razorpayOrderId + "|" + razorpayPaymentId;

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] hash = sha256Hmac.doFinal(data.getBytes());

            String generatedSignature = Hex.encodeHexString(hash);
            System.out.println("Order ID: " + razorpayOrderId);
            System.out.println("Payment ID: " + razorpayPaymentId);
            System.out.println("Signature from Razorpay: " + razorpaySignature);
            System.out.println("Data used: " + razorpayOrderId + "|" + razorpayPaymentId);
            System.out.println("Secret: " + SECRET_KEY);

            if (generatedSignature.equals(razorpaySignature)) {
                verifyPayment.verifyPayment(razorpayPaymentId,orderId,phoneNumber,true);

                return ResponseEntity.ok(Map.of("status", "success"));
            } else {
                verifyPayment.verifyPayment(razorpayPaymentId,orderId,phoneNumber,false);
                return ResponseEntity.ok(Map.of("status", "failed"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("status", "error"));
        }
    }
}
