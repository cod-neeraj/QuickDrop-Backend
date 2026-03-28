package com.example.User.Repository;

import com.example.User.Models.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishListRepo extends JpaRepository<WishList,Long> {
}
