package com.mays.srm.enquiry.repository;
import com.mays.srm.enquiry.entities.Enquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EnquiryDao extends JpaRepository<Enquiry, Integer> {
    List<Enquiry> findByUserUserId(Integer userId);

    @Query("SELECT COUNT(e) FROM Enquiry e WHERE e.status IS NOT NULL " +
           "AND (LOWER(e.status.statusName) LIKE '%pending%' OR LOWER(e.status.statusName) LIKE '%open%')")
    long countPendingEnquiries();

    @Query("SELECT COUNT(e) FROM Enquiry e WHERE e.user.userId = :userId AND e.status IS NOT NULL " +
           "AND (LOWER(e.status.statusName) LIKE '%pending%' OR LOWER(e.status.statusName) LIKE '%open%')")
    long countPendingEnquiriesByUser(@Param("userId") Integer userId);
}

