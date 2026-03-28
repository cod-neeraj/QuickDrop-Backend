package com.example.Delivery.Repository;

import com.example.Delivery.Models.ActiveDelivery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActiveDeliveryRepo extends MongoRepository<ActiveDelivery,String> {

    boolean existsByOrderId(String orderId);


}
