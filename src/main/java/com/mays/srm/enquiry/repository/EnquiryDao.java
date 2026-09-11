package com.mays.srm.enquiry.repository;

import com.mays.srm.enquiry.entities.Enquiry;
import com.mays.srm.enquiry.enums.EnquiryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface EnquiryDao extends JpaRepository<Enquiry, Integer> {

    // Count all enquiries with status not in final/completed states
    @Query("SELECT COUNT(e) FROM Enquiry e WHERE e.status NOT IN :completedStatuses")
    long countPendingEnquiries(@Param("completedStatuses") List<EnquiryStatus> completedStatuses);

    // Count pending for specific user
    @Query("SELECT COUNT(e) FROM Enquiry e WHERE e.user.userId = :userId AND e.status NOT IN :completedStatuses")
    long countPendingEnquiriesByUser(@Param("userId") Integer userId,
                                     @Param("completedStatuses") List<EnquiryStatus> completedStatuses);

    List<Enquiry> findByUserUserId(Integer userId);
    Optional<Enquiry> findByConvertedTicketTicketId(@Param("ticketId") Integer ticketId);
}