package com.mays.srm.security.controller;

import com.mays.srm.security.service.*;
import com.mays.srm.security.core.*;
import com.mays.srm.notification.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mays.srm.user.dto.reqDTO.UserMasterRequestDTO;
import com.mays.srm.user.dto.resDTO.UserMasterResponseDTO;
import com.mays.srm.user.service.UserMasterService;
import com.mays.srm.organization.service.BranchService;
import com.mays.srm.security.entities.SecurityProfile;
import com.mays.srm.security.entities.ActiveSession;
import com.mays.srm.security.repository.ActiveSessionDao;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import com.mays.srm.user.entities.Employee;
import com.mays.srm.user.entities.UserMaster;
import com.mays.srm.user.entities.Vendor;
import com.mays.srm.user.repository.EmployeeDao;
import com.mays.srm.user.repository.UserMasterDao;
import com.mays.srm.user.repository.VendorDao;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserMasterService userMasterService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private AuthService authService;

    @Autowired
    private SecurityProfileService securityProfileService;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private UserMasterDao userMasterDao;

    @Autowired
    private VendorDao vendorDao;

    @Autowired
    private ActiveSessionDao activeSessionDao;

    @Autowired
    private OtpService otpService;

    /**
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> requestData, HttpServletRequest request) {
        String mobileNo = requestData.get("mobileNo");
        String password = requestData.get("password");

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(mobileNo, password));
        } catch (LockedException e) {
            return ResponseEntity.status(403).body(Map.of("error", "Account is locked. Please try again later."));
        } catch (BadCredentialsException e) {
            System.out.println("Bad Credentials");
            Optional<Employee> empOpt = employeeDao.findByMobileNo(mobileNo);
            if (empOpt.isPresent()) {
                SecurityProfile profile = securityProfileService.getOrCreateProfileForEmployee(empOpt.get());
                securityProfileService.recordFailedAttempt(profile);
            } else {
                Optional<UserMaster> userOpt = userMasterDao.findByMobileNo(mobileNo);
                if (userOpt.isPresent()) {
                    SecurityProfile profile = securityProfileService.getOrCreateProfileForUser(userOpt.get());
                    securityProfileService.recordFailedAttempt(profile);
                } else {
                    Optional<Vendor> vendorOpt = vendorDao.findByMobileNo(mobileNo);
                    if (vendorOpt.isPresent()) {
                        SecurityProfile profile = securityProfileService.getOrCreateProfileForVendor(vendorOpt.get());
                        securityProfileService.recordFailedAttempt(profile);
                    }
                }
            }
            return ResponseEntity.status(401).body(Map.of("error", "Invalid mobile number or password."));
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(mobileNo);

        CustomUserDetails customUser = (CustomUserDetails) userDetails;
        Optional<Employee> empOpt = employeeDao.findByMobileNo(mobileNo);
        Optional<UserMaster> userOpt = Optional.empty();
        Optional<Vendor> vendorOpt = Optional.empty();

        if (empOpt.isPresent()) {
            securityProfileService
                    .resetFailedAttempts(securityProfileService.getOrCreateProfileForEmployee(empOpt.get()));
        } else {
            userOpt = userMasterDao.findByMobileNo(mobileNo);
            if (userOpt.isPresent()) {
                securityProfileService
                        .resetFailedAttempts(securityProfileService.getOrCreateProfileForUser(userOpt.get()));
            } else {
                vendorOpt = vendorDao.findByMobileNo(mobileNo);
                if (vendorOpt.isPresent()) {
                    securityProfileService
                            .resetFailedAttempts(securityProfileService.getOrCreateProfileForVendor(vendorOpt.get()));
                }
            }
        }

        if (customUser.isFirstTimeLogin()) {
            return ResponseEntity.status(403).body(Map.of("error", "FORCE_PASSWORD_CHANGE"));
        }

        String sessionId = UUID.randomUUID().toString();
        ActiveSession session = ActiveSession.builder()
                .sessionId(sessionId)
                .loginIp(getClientIp(request))
                .deviceInfo(request.getHeader("User-Agent"))
                .expiresAt(
                        LocalDateTime.now().plus(jwtService.getJwtExpiration(), java.time.temporal.ChronoUnit.MILLIS))
                .isActive(true)
                .build();

        if (empOpt.isPresent()) {
            session.setEmployee(empOpt.get());
        } else if (userOpt.isPresent()) {
            session.setUser(userOpt.get());
        } else if (vendorOpt.isPresent()) {
            session.setVendor(vendorOpt.get());
        }
        activeSessionDao.save(session);

        final String jwt = jwtService.generateToken(userDetails, sessionId);

        return ResponseEntity.ok(Map.of("token", jwt));
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        Object userProfile = authService.getCurrentUserProfile(principal.getName());
        if (userProfile != null) {
            return ResponseEntity.ok(userProfile);
        }
        return ResponseEntity.status(404).body("User not found");
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserMasterRequestDTO requestDTO) {
        try {
            if (!otpService.isOtpVerified(requestDTO.getEmailId(), "REGISTER")) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", "Email is not verified via OTP. Please verify OTP first."));
            }
            UserMasterResponseDTO responseDTO = userMasterService.create(requestDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("emailId");
        String purpose = request.get("purpose");
        try {
            otpService.generateAndSendOtp(email, purpose);
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("emailId");
        String otp = request.get("otp");
        String purpose = request.get("purpose");
        try {
            otpService.validateOtp(email, otp, purpose);
            return ResponseEntity.ok(Map.of("message", "OTP verified successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/branches")
    public ResponseEntity<?> getAllBranches() {
        try {
            return ResponseEntity.ok(branchService.getAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(Principal principal, @RequestBody Map<String, Object> request) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        Object updatedProfile = authService.updateCurrentUserProfile(principal.getName(), request);
        if (updatedProfile != null) {
            return ResponseEntity.ok(updatedProfile);
        }
        return ResponseEntity.status(404).body("User not found");
    }

    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<?> forgotPasswordSendOtp(@RequestBody Map<String, String> request) {
        String mobileNo = request.get("mobileNo");

        String email = null;
        Optional<Employee> empOpt = employeeDao.findByMobileNo(mobileNo);
        if (empOpt.isPresent()) {
            email = empOpt.get().getEmail();
        } else {
            Optional<UserMaster> userOpt = userMasterDao.findByMobileNo(mobileNo);
            if (userOpt.isPresent()) {
                email = userOpt.get().getEmailId();
            } else {
                Optional<Vendor> vendorOpt = vendorDao.findByMobileNo(mobileNo);
                if (vendorOpt.isPresent()) {
                    email = vendorOpt.get().getEmail();
                }
            }
        }

        if (email == null) {
            return ResponseEntity.status(404).body(Map.of("error", "No account found with this mobile number."));
        }

        try {
            otpService.generateAndSendOtpWithMobile(email, mobileNo, "FORGOT_PASSWORD");
            String maskedEmail = email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
            return ResponseEntity.ok(Map.of(
                    "message", "OTP sent successfully",
                    "email", maskedEmail
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<?> forgotPasswordVerifyOtp(@RequestBody Map<String, String> request) {
        String mobileNo = request.get("mobileNo");
        String otp = request.get("otp");
        try {
            otpService.validateOtpForMobile(mobileNo, otp, "FORGOT_PASSWORD");
            return ResponseEntity.ok(Map.of("message", "OTP verified successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String mobileNo = request.get("mobileNo");
        String newPassword = request.get("newPassword");

        if (!otpService.isOtpVerifiedForMobile(mobileNo, "FORGOT_PASSWORD")) {
            return ResponseEntity.status(403).body(Map.of("error", "OTP not verified or expired."));
        }

        try {
            Optional<Employee> empOpt = employeeDao.findByMobileNo(mobileNo);
            if (empOpt.isPresent()) {
                Employee emp = empOpt.get();
                emp.setPassword(passwordEncoder.encode(newPassword));
                employeeDao.save(emp);
                return ResponseEntity.ok(Map.of("message", "Password reset successfully."));
            }

            Optional<UserMaster> userOpt = userMasterDao.findByMobileNo(mobileNo);
            if (userOpt.isPresent()) {
                UserMaster user = userOpt.get();
                user.setPassword(passwordEncoder.encode(newPassword));
                userMasterDao.save(user);
                return ResponseEntity.ok(Map.of("message", "Password reset successfully."));
            }

            Optional<Vendor> vendorOpt = vendorDao.findByMobileNo(mobileNo);
            if (vendorOpt.isPresent()) {
                Vendor vendor = vendorOpt.get();
                vendor.setPassword(passwordEncoder.encode(newPassword));
                vendorDao.save(vendor);
                return ResponseEntity.ok(Map.of("message", "Password reset successfully."));
            }

            return ResponseEntity.status(404).body(Map.of("error", "User not found."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // NEW: FORCE CHANGE PASSWORD (first-time-login flow)
    // POST /api/auth/force-change-password
    // Body: { mobileNo, currentPassword, newPassword }
    // ═══════════════════════════════════════════════════════════════
    @PostMapping("/force-change-password")
    public ResponseEntity<?> forceChangePassword(@RequestBody Map<String, String> request) {
        String mobileNo = request.get("mobileNo");
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");
        System.out.println("reach here 1 ");

        if (mobileNo == null || currentPassword == null || newPassword == null
                || mobileNo.isBlank() || currentPassword.isBlank() || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields."));
        }
        if (newPassword.length() < 8) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "New password must be at least 8 characters."));
        }
        if (newPassword.equals(currentPassword)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "New password must be different from current password."));
        }

        try {
            // 1. Try Employee
            Optional<Employee> empOpt = employeeDao.findByMobileNo(mobileNo);
            if (empOpt.isPresent()) {
                Employee emp = empOpt.get();
                if (!passwordEncoder.matches(currentPassword, emp.getPassword())) {
                     return ResponseEntity.status(400).body(Map.of("error", "Current password is incorrect."));
                }
                emp.setPassword(passwordEncoder.encode(newPassword));
                employeeDao.save(emp);
                securityProfileService.markPasswordChanged(
                        securityProfileService.getOrCreateProfileForEmployee(emp));
                return ResponseEntity.ok(Map.of("message", "Password changed successfully."));
            }

            // 2. Try UserMaster
            Optional<UserMaster> userOpt = userMasterDao.findByMobileNo(mobileNo);
            if (userOpt.isPresent()) {
                UserMaster user = userOpt.get();
                if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                     return ResponseEntity.status(400).body(Map.of("error", "Current password is incorrect."));
                }
                user.setPassword(passwordEncoder.encode(newPassword));
                userMasterDao.save(user);
                securityProfileService.markPasswordChanged(
                        securityProfileService.getOrCreateProfileForUser(user));
                return ResponseEntity.ok(Map.of("message", "Password changed successfully."));
            }

            // 3. Try Vendor
            Optional<Vendor> vendorOpt = vendorDao.findByMobileNo(mobileNo);
            if (vendorOpt.isPresent()) {
                Vendor vendor = vendorOpt.get();
                if (!passwordEncoder.matches(currentPassword, vendor.getPassword())) {
                     return ResponseEntity.status(400).body(Map.of("error", "Current password is incorrect."));
                }
                vendor.setPassword(passwordEncoder.encode(newPassword));
                vendorDao.save(vendor);
                securityProfileService.markPasswordChanged(
                        securityProfileService.getOrCreateProfileForVendor(vendor));
                return ResponseEntity.ok(Map.of("message", "Password changed successfully."));
            }

            return ResponseEntity.status(404).body(Map.of("error", "User not found."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}