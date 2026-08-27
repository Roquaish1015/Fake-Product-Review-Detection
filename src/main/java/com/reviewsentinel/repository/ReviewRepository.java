package com.reviewsentinel.repository;

import com.reviewsentinel.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByOrderByCreatedAtDesc();

    List<Review> findByVerdictOrderByCreatedAtDesc(String verdict);

    @Query("SELECT r FROM Review r WHERE LOWER(r.productName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(r.reviewContent) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(r.reviewTitle) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY r.createdAt DESC")
    List<Review> searchReviews(@Param("query") String query);

    long countByVerdict(String verdict);

    long countBySentiment(String sentiment);

    long countByRiskLevel(String riskLevel);
}
