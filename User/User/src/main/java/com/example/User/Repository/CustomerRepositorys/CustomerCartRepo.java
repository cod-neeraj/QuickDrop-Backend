package com.example.User.Repository.CustomerRepositorys;

import com.example.User.Models.Customer.Customer_Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerCartRepo extends JpaRepository<Customer_Cart,Long> {
}
