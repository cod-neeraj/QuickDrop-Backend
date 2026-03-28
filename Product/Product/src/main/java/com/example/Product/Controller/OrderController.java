package com.example.Product.Controller;

import com.example.Product.DTO.RazorPayOrderDetails;
import com.example.Product.DTO.UserOrderDetails;
import com.example.Product.Service.KafkaConsumer;
import com.example.Product.Service.KafkaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.codec.binary.Hex;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    KafkaService kafkaService;

    @Autowired
    KafkaConsumer kafkaConsumer;
//    @PostMapping("/send")
//    public ResponseEntity<?> sendRequest(){
//       kafkaService.sendDeliveryBoyRequest("rohan");
//       return ResponseEntity.ok(true);
//    }
private static final String SECRET_KEY = "zRdyCfcV3YR3gal5KYVVFwB4";

    @PostMapping("/placeOrder")
    public ResponseEntity<RazorPayOrderDetails> placeOrder(@RequestBody @Valid UserOrderDetails userOrderDetails) throws JsonProcessingException, RazorpayException {
//        RazorPayOrderDetails list = kafkaConsumer.consumeProduct(userOrderDetails);
        return ResponseEntity.ok(null);
    }
    // from Razorpay Dashboard
// add dependency commons-codec

    @PostMapping("/verifyPayment")
    public ResponseEntity<Map<String, String>> verifyPayment(@RequestBody Map<String, String> payload) {
        try {
            String razorpayOrderId = payload.get("razorpay_order_id");
            String razorpayPaymentId = payload.get("razorpay_payment_id");
            String razorpaySignature = payload.get("razorpay_signature");

            String data = razorpayOrderId + "|" + razorpayPaymentId;

            // 🔹 Generate HMAC-SHA256 signature using secret
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] hash = sha256Hmac.doFinal(data.getBytes());

            // Convert to HEX string (not Base64)
            String generatedSignature = Hex.encodeHexString(hash);
            System.out.println("Order ID: " + razorpayOrderId);
            System.out.println("Payment ID: " + razorpayPaymentId);
            System.out.println("Signature from Razorpay: " + razorpaySignature);
            System.out.println("Data used: " + razorpayOrderId + "|" + razorpayPaymentId);
            System.out.println("Secret: " + SECRET_KEY);

            if (generatedSignature.equals(razorpaySignature)) {
                kafkaConsumer.saveAfterSuccessOfPayment(razorpayOrderId);
                return ResponseEntity.ok(Map.of("status", "success"));
            } else {
                return ResponseEntity.ok(Map.of("status", "failed"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("status", "error"));
        }
    }

}
