package com.example.User.Repository;

import com.example.User.Models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepo extends JpaRepository<Cart,Long> {
}
