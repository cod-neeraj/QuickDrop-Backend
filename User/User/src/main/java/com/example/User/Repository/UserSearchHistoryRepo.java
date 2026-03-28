package com.example.User.Repository;

import com.example.User.Models.UserSearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSearchHistoryRepo extends JpaRepository<UserSearchHistory,Integer> {

//    Optional<List<UserSearchHistory>> findByUserId(String id);
}
