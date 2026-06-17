package com.mays.srm.security;

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

import com.mays.srm.dao.core.EmployeeDao;
import com.mays.srm.dao.core.UserMasterDao;
import com.mays.srm.entity.Employee;
import com.mays.srm.entity.UserMaster;
import com.mays.srm.entity.Branch;
import com.mays.srm.entity.Department;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import com.mays.srm.dto.responseDTO.EmployeeResponseDTO;
import com.mays.srm.dto.responseDTO.UserMasterResponseDTO;
import org.modelmapper.ModelMapper;
import com.mays.srm.service.UserMasterService;
import com.mays.srm.service.BranchService;
import com.mays.srm.service.EmployeeService;
import com.mays.srm.dto.requestDTO.UserMasterRequestDTO;
import com.mays.srm.dto.requestDTO.EmployeeRequestDTO;
import com.mays.srm.dto.responseDTO.BranchResponseDTO;

// STEP 6: THE TICKET BOOTH (AuthController)
// The user needs an actual URL (`/api/auth/login`) to send their mobile number and password to.
// This is a standard REST Controller that receives the JSON payload.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager; // The Boss who checks the password

    @Autowired
    private JwtService jwtService; // The Wristband Maker

    @Autowired
    private UserDetailsService userDetailsService; // The Database Clerk

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private UserMasterDao userMasterDao;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserMasterService userMasterService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private BranchService branchService;

    /**
     * POST /api/auth/login
     * User sends JSON: { "mobileNo": "123", "password": "password" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        // 1. Grab what the user typed in
        String mobileNo = request.get("mobileNo");
        String password = request.get("password");

        // 2. Tell the Boss (AuthenticationManager) to check if the credentials match.
        // It automatically goes to the DB (via UserDetailsServiceImpl) and hashes the password to verify.
        // If the password is wrong or user is locked out, it automatically throws a 403 Forbidden Exception here!
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(mobileNo, password)
        );

        // 3. If it reaches this line, the login was successful! 
        // We fetch their full details (Role, Active Status)
        final UserDetails userDetails = userDetailsService.loadUserByUsername(mobileNo);

        // 4. We ask the Maker to create a shiny new Token for this user
        final String jwt = jwtService.generateToken(userDetails);

        // 5. Return the Token to the User's browser/app as a JSON response!
        return ResponseEntity.ok(Map.of("token", jwt));
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
        String mobileNo = principal.getName();

        // 1. Check Employee table
        Optional<Employee> employeeOpt = employeeDao.findByMobileNo(mobileNo);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            EmployeeResponseDTO dto = modelMapper.map(employee, EmployeeResponseDTO.class);
            if (employee.getDepartment() != null) {
                dto.setDepartmentName(employee.getDepartment().getDepartmentName());
            }
            return ResponseEntity.ok(dto);
        }

        // 2. Check User table
        Optional<UserMaster> userOpt = userMasterDao.findByMobileNo(mobileNo);
        if (userOpt.isPresent()) {
            UserMaster user = userOpt.get();
            UserMasterResponseDTO dto = modelMapper.map(user, UserMasterResponseDTO.class);
            if (user.getBranch() != null) {
                dto.setBranchName(user.getBranch().getBranchName());
            }
            return ResponseEntity.ok(dto);
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
            UserMasterResponseDTO responseDTO = userMasterService.create(requestDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
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
        String mobileNo = principal.getName();

        // 1. Check Employee table
        Optional<Employee> employeeOpt = employeeDao.findByMobileNo(mobileNo);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            EmployeeRequestDTO requestDTO = new EmployeeRequestDTO();
            requestDTO.setEmployeeName((String) request.get("employeeName"));
            requestDTO.setMobileNo((String) request.get("mobileNo"));
            requestDTO.setEmail((String) request.get("email"));
            requestDTO.setAddress((String) request.get("address"));
            requestDTO.setVendor((String) request.get("vendor"));
            requestDTO.setPincode((String) request.get("pincode"));
            requestDTO.setCity((String) request.get("city"));
            
            if (employee.getDepartment() != null) {
                requestDTO.setDepartmentId(employee.getDepartment().getDepartmentId());
            }
            requestDTO.setIsActive(employee.getIsActive());

            EmployeeResponseDTO updated = employeeService.update(employee.getEmployeeId(), requestDTO);
            return ResponseEntity.ok(updated);
        }

        // 2. Check User table
        Optional<UserMaster> userOpt = userMasterDao.findByMobileNo(mobileNo);
        if (userOpt.isPresent()) {
            UserMaster user = userOpt.get();
            UserMasterRequestDTO requestDTO = new UserMasterRequestDTO();
            requestDTO.setFirstName((String) request.get("firstName"));
            requestDTO.setLastName((String) request.get("lastName"));
            requestDTO.setMobileNo((String) request.get("mobileNo"));
            requestDTO.setEmailId((String) request.get("emailId"));
            requestDTO.setAddress((String) request.get("address"));
            
            if (user.getBranch() != null) {
                requestDTO.setBranchId(user.getBranch().getBranchId());
            }

            UserMasterResponseDTO updated = userMasterService.update(user.getUserId(), requestDTO);
            return ResponseEntity.ok(updated);
        }

        return ResponseEntity.status(404).body("User not found");
    }
}
