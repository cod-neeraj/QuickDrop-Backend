package com.example.User.Repository.CustomerRepositorys;

import com.example.User.Models.Customer.Customer_Visited_product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Customer_visited_product_Repo extends MongoRepository<Customer_Visited_product,String> {

    Optional<Customer_Visited_product> findByCustomerIdAndProductId(String customerId,String productId);
    List<Customer_Visited_product> findByCustomerId(String customerId);

}
