package com.mays.srm.notification.service;

import com.mays.srm.notification.entities.OtpTracking;
import com.mays.srm.notification.repository.OtpTrackingDao;
import org.springframework.beans.factory.annotation.Autowired;
import com.mays.srm.notification.entities.NotificationOutbox;
import com.mays.srm.notification.scheduler.NotificationScheduler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpTrackingDao otpTrackingDao;

    @Autowired
    private NotificationScheduler notificationScheduler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void generateAndSendOtp(String email, String purpose) {
        // Generate a 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Save to DB
        OtpTracking otpTracking = OtpTracking.builder()
                .emailId(email)
                .mobileNo("0000000000") // Defaulting mobile as we are focusing on email for now
                .otpHash(passwordEncoder.encode(otp))
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(5)) // 5 minutes expiry
                .isVerified(false)
                .build();

        otpTrackingDao.save(otpTracking);

        // Build Outbox object and send synchronously via scheduler without saving to DB
        NotificationOutbox outbox = NotificationOutbox.builder()
                .recipient(email)
                .subject("Your OTP Code - MAYS SRM")
                .templateId("global_otp")
                .messageBody("{\"otp\": \"" + otp + "\", \"purpose\": \"" + purpose + "\"}")
                .type("EMAIL")
                .status("PENDING")
                .retryCount(0)
                .build();

        try {
            notificationScheduler.sendEmail(outbox);
        } catch (Exception e) {
            System.err.println("Failed to send MSG91 email: " + e.getMessage());
            throw new RuntimeException("Failed to send OTP email. Please check your configuration.");
        }
    }

    public boolean validateOtp(String email, String otp, String purpose) {
        Optional<OtpTracking> optionalOtp = otpTrackingDao.findTopByEmailIdAndPurposeOrderByCreatedAtDesc(email,purpose);

        if (optionalOtp.isEmpty()) {
            throw new RuntimeException("No OTP requested for this email.");
        }

        OtpTracking otpTracking = optionalOtp.get();

        if (otpTracking.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired.");
        }

        if (otpTracking.getIsVerified()) {
            return true; // Already verified
        }

        if (passwordEncoder.matches(otp, otpTracking.getOtpHash())) {
            otpTracking.setIsVerified(true);
            otpTrackingDao.save(otpTracking);
            return true;
        } else {
            otpTracking.setFailedAttempts(otpTracking.getFailedAttempts() + 1);
            otpTrackingDao.save(otpTracking);
            throw new RuntimeException("Invalid OTP.");
        }
    }

    public boolean isOtpVerified(String email, String purpose) {
        Optional<OtpTracking> optionalOtp = otpTrackingDao.findTopByEmailIdAndPurposeOrderByCreatedAtDesc(email,
                purpose);
        if (optionalOtp.isPresent()) {
            OtpTracking otp = optionalOtp.get();
            return otp.getIsVerified() && otp.getExpiresAt().isAfter(LocalDateTime.now().minusMinutes(30)); // Give some
                                                                                                            // grace
                                                                                                            // period
                                                                                                            // for
                                                                                                            // registration
                                                                                                            // submission
        }
        return false;
    }

    public void generateAndSendOtpWithMobile(String email, String mobileNo, String purpose) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        OtpTracking otpTracking = OtpTracking.builder()
                .emailId(email)
                .mobileNo(mobileNo)
                .otpHash(passwordEncoder.encode(otp))
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .isVerified(false)
                .build();
                
        otpTrackingDao.save(otpTracking);
        
        NotificationOutbox outbox = NotificationOutbox.builder()
                .recipient(email)
                .subject("Your OTP Code - MAYS SRM")
                .templateId("global_otp")
                .messageBody("{\"otp\": \"" + otp + "\", \"purpose\": \"" + purpose + "\"}")
                .type("EMAIL")
                .status("PENDING")
                .retryCount(0)
                .build();
                
        try {
            notificationScheduler.sendEmail(outbox);
        } catch (Exception e) {
            System.err.println("Failed to send MSG91 email: " + e.getMessage());
            throw new RuntimeException("Failed to send OTP email. Please check your configuration.");
        }
    }

    public boolean validateOtpForMobile(String mobileNo, String otp, String purpose) {
        Optional<OtpTracking> optionalOtp = otpTrackingDao.findTopByMobileNoAndPurposeOrderByCreatedAtDesc(mobileNo, purpose);
        
        if (optionalOtp.isEmpty()) {
            throw new RuntimeException("No OTP requested for this mobile number.");
        }
        
        OtpTracking otpTracking = optionalOtp.get();
        
        if (otpTracking.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired.");
        }
        
        if (otpTracking.getIsVerified()) {
            return true;
        }
        
        if (passwordEncoder.matches(otp, otpTracking.getOtpHash())) {
            otpTracking.setIsVerified(true);
            otpTrackingDao.save(otpTracking);
            return true;
        } else {
            otpTracking.setFailedAttempts(otpTracking.getFailedAttempts() + 1);
            otpTrackingDao.save(otpTracking);
            throw new RuntimeException("Invalid OTP.");
        }
    }

    public boolean isOtpVerifiedForMobile(String mobileNo, String purpose) {
        Optional<OtpTracking> optionalOtp = otpTrackingDao.findTopByMobileNoAndPurposeOrderByCreatedAtDesc(mobileNo, purpose);
        if (optionalOtp.isPresent()) {
            OtpTracking otp = optionalOtp.get();
            return otp.getIsVerified() && otp.getExpiresAt().isAfter(LocalDateTime.now().minusMinutes(30));
        }
        return false;
    }
}
