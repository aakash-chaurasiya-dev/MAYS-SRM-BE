package com.mays.srm.security.service;
import com.mays.srm.security.core.*;
import com.mays.srm.user.entities.Employee;
import com.mays.srm.user.entities.UserMaster;
import com.mays.srm.user.entities.Vendor;
import com.mays.srm.user.repository.EmployeeDao;
import com.mays.srm.user.repository.UserMasterDao;
import com.mays.srm.user.repository.VendorDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

// STEP 2: THE DATABASE CLERK
// Spring Security says: "I have a mobile number '98765', go find their data!"
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMasterDao userMasterDao; // The User database

    @Autowired
    private EmployeeDao employeeDao; // The Employee database

    @Autowired
    private VendorDao vendorDao; // The Vendor database

    @Autowired
    private SecurityProfileService securityProfileService;

    // This is the ONE method Spring calls during login to find the user
    @Override
    public UserDetails loadUserByUsername(String mobileNo) throws UsernameNotFoundException {

        // 1. Ask the Employee table first: "Do you have anyone with this mobile number?"
        Optional<Employee> employeeOpt = employeeDao.findByMobileNo(mobileNo);
        if (employeeOpt.isPresent()) {
            Employee emp = employeeOpt.get(); // We found an employee!
            // Get their security profile
            com.mays.srm.security.entities.SecurityProfile profile = securityProfileService.getOrCreateProfileForEmployee(emp);
            boolean isLocked = securityProfileService.isAccountLocked(profile);
            
            // We convert the Employee into our CustomUserDetails (The Passport)
            return new CustomUserDetails(emp.getMobileNo(), emp.getPassword(), emp.getRole(), emp.getIsActive(), emp.getEmployeeName(), emp.getEmployeeId(), !isLocked, profile.getFirstTimeLogin());
        }

        // 2. If it wasn't an employee, ask the User table: "Do you have this mobile number?"
        Optional<UserMaster> userOpt = userMasterDao.findByMobileNo(mobileNo);
        if (userOpt.isPresent()) {
            UserMaster user = userOpt.get(); // We found a normal user!
            
            // Get their security profile
            com.mays.srm.security.entities.SecurityProfile profile = securityProfileService.getOrCreateProfileForUser(user);
            boolean isLocked = securityProfileService.isAccountLocked(profile);

            // We convert the User into our CustomUserDetails (The Passport)
            String fullName = user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : "");
            return new CustomUserDetails(user.getMobileNo(), user.getPassword(), user.getRole(), user.getIsActive(), fullName, user.getUserId(), !isLocked, profile.getFirstTimeLogin());
        }

        // 3. Ask the Vendor table: "Do you have this mobile number?"
        Optional<Vendor> vendorOpt = vendorDao.findByMobileNo(mobileNo);
        if (vendorOpt.isPresent()) {
            Vendor vendor = vendorOpt.get(); // We found a vendor!
            
            // Get their security profile
            com.mays.srm.security.entities.SecurityProfile profile = securityProfileService.getOrCreateProfileForVendor(vendor);
            boolean isLocked = securityProfileService.isAccountLocked(profile);

            // We convert the Vendor into our CustomUserDetails
            return new CustomUserDetails(vendor.getMobileNo(), vendor.getPassword(), vendor.getRoleName(), vendor.getIsActive(), vendor.getName(), vendor.getId(), !isLocked, profile.getFirstTimeLogin());
        }

        // 4. We didn't find anyone in any table! Deny login.
        throw new UsernameNotFoundException("No one found with mobile number: " + mobileNo);
    }
}
