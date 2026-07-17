package com.mays.srm.notification.repository;

import com.mays.srm.notification.entities.OtpTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpTrackingDao extends JpaRepository<OtpTracking, Integer> {
    Optional<OtpTracking> findByMobileNoAndPurposeAndIsVerifiedFalse(String mobileNo, String purpose);
    Optional<OtpTracking> findByEmailIdAndPurposeAndIsVerifiedFalse(String emailId, String purpose);
    Optional<OtpTracking> findTopByEmailIdAndPurposeOrderByCreatedAtDesc(String emailId, String purpose);
    Optional<OtpTracking> findTopByMobileNoAndPurposeOrderByCreatedAtDesc(String mobileNo, String purpose);
}
