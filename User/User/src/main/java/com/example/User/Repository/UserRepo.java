package com.example.User.Repository;

import com.example.User.DataToShow.PersonalInfo;
import com.example.User.Models.Users;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users,Long> {
    Optional<Users> findByEmailId(String emailId);
    @Query("SELECT new com.example.User.DataToShow.PersonalInfo(p.id, p.name, p.emailId, p.dob, p.gender) " +
            "FROM Users p WHERE p.emailId = :emailId")
    PersonalInfo findPersonalInfo(@Param("emailId") String emailId);



}