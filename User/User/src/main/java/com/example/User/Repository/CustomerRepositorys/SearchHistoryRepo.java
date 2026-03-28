package com.example.User.Repository.CustomerRepositorys;

import com.example.User.DataToShow.Search_History_frontend;
import com.example.User.Models.Customer.Search_History;
import com.example.User.Models.UserSearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchHistoryRepo extends JpaRepository<Search_History, Long> {

    @Query(value = """
        SELECT *
        FROM search_history s
        WHERE s.user_id = :id
        ORDER BY s.date DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Search_History> findLatestFiveSearches(@Param("id") String id);
}

/*
DESC LIMIT 5
 */
