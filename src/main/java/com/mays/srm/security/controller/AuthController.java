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
import com.mays.srm.user.dto.resDTO.UserMasterResponseDTO;
import com.mays.srm.user.service.UserMasterService;
import com.mays.srm.organization.service.BranchService;
import com.mays.srm.user.dto.request.UserMasterRequestDTO;
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
import com.mays.srm.user.repository.EmployeeDao;
import com.mays.srm.user.repository.UserMasterDao;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

// STEP 6: THE TICKET BOOTH (AuthController)
// The user needs an actual URL (`/api/auth/login`) to send their mobile number and password to.
// This is a standard REST Controller that receives the JSON payload.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager; // The Boss who checks the password
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService; // The Wristband Maker

    @Autowired
    private UserDetailsService userDetailsService; // The Database Clerk

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
    private ActiveSessionDao activeSessionDao;

    @Autowired
    private OtpService otpService;

    /**
     * POST /api/auth/login
     * User sends JSON: { "mobileNo": "123", "password": "password" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> requestData, HttpServletRequest request) {
        // 1. Grab what the user typed in
        String mobileNo = requestData.get("mobileNo");
        String password = requestData.get("password");

        // 2. Tell the Boss (AuthenticationManager) to check if the credentials match.
        // It automatically goes to the DB (via UserDetailsServiceImpl) and hashes the
        // password to verify.
        // If the password is wrong or user is locked out, it automatically throws a 403
        // Forbidden Exception here!
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(mobileNo, password));
        } catch (LockedException e) {
            return ResponseEntity.status(403).body(Map.of("error", "Account is locked. Please try again later."));
        } catch (BadCredentialsException e) {
            // Find user and increment failed attempts
            Optional<Employee> empOpt = employeeDao.findByMobileNo(mobileNo);
            if (empOpt.isPresent()) {
                SecurityProfile profile = securityProfileService.getOrCreateProfileForEmployee(empOpt.get());
                securityProfileService.recordFailedAttempt(profile);
            } else {
                Optional<UserMaster> userOpt = userMasterDao.findByMobileNo(mobileNo);
                if (userOpt.isPresent()) {
                    SecurityProfile profile = securityProfileService.getOrCreateProfileForUser(userOpt.get());
                    securityProfileService.recordFailedAttempt(profile);
                }
            }
            return ResponseEntity.status(401).body(Map.of("error", "Invalid mobile number or password."));
        }

        // 3. If it reaches this line, the login was successful!
        // We fetch their full details (Role, Active Status)
        final UserDetails userDetails = userDetailsService.loadUserByUsername(mobileNo);

        // Reset failed attempts on successful login
        CustomUserDetails customUser = (CustomUserDetails) userDetails;
        Optional<Employee> empOpt = employeeDao.findByMobileNo(mobileNo);
        Optional<UserMaster> userOpt = Optional.empty();

        if (empOpt.isPresent()) {
            securityProfileService
                    .resetFailedAttempts(securityProfileService.getOrCreateProfileForEmployee(empOpt.get()));
        } else {
            userOpt = userMasterDao.findByMobileNo(mobileNo);
            if (userOpt.isPresent()) {
                securityProfileService
                        .resetFailedAttempts(securityProfileService.getOrCreateProfileForUser(userOpt.get()));
            }
        }

        // Check first time login flag
        if (customUser.isFirstTimeLogin()) {
            return ResponseEntity.status(403).body(Map.of("error", "FORCE_PASSWORD_CHANGE"));
        }

        // Create Active Session
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
        }
        activeSessionDao.save(session);

        // 4. We ask the Maker to create a shiny new Token for this user
        final String jwt = jwtService.generateToken(userDetails, sessionId);

        // 5. Return the Token to the User's browser/app as a JSON response!
        return ResponseEntity.ok(Map.of("token", jwt));
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    /**
     * GET /api/auth/me
     * Returns the full details of the currently authenticated user
     */
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

    /**
     * POST /api/auth/register
     * Public endpoint to register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserMasterRequestDTO requestDTO) {
        try {
            // Verify if OTP was validated for this email
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

    /**
     * POST /api/auth/send-otp
     * Endpoint to send OTP to an email
     */
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("emailId");
        String purpose = request.get("purpose"); // "REGISTER" or "FORGOT_PASSWORD"
        try {
            otpService.generateAndSendOtp(email, purpose);
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/auth/verify-otp
     * Endpoint to verify an OTP
     */
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

    /**
     * GET /api/auth/branches
     * Public endpoint to fetch all branches for user registration select dropdown
     */
    @GetMapping("/branches")
    public ResponseEntity<?> getAllBranches() {
        try {
            return ResponseEntity.ok(branchService.getAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/auth/me
     * Updates the currently authenticated user's profile details
     */
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

    /**
     * POST /api/auth/forgot-password/send-otp
     */
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

    /**
     * POST /api/auth/forgot-password/verify-otp
     */
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

    /**
     * POST /api/auth/forgot-password/reset
     */
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
            
            return ResponseEntity.status(404).body(Map.of("error", "User not found."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
