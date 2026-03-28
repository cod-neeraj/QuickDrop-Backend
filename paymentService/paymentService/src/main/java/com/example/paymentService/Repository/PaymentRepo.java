package com.example.paymentService.Repository;

import com.example.paymentService.Model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends MongoRepository<Payment,Long> {
}
