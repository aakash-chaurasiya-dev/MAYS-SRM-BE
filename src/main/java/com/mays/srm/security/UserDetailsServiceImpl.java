package com.mays.srm.security;

import com.mays.srm.dao.core.EmployeeDao;
import com.mays.srm.dao.core.UserMasterDao;
import com.mays.srm.entity.Employee;
import com.mays.srm.entity.UserMaster;
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

    // This is the ONE method Spring calls during login to find the user
    @Override
    public UserDetails loadUserByUsername(String mobileNo) throws UsernameNotFoundException {

        // 1. Ask the Employee table first: "Do you have anyone with this mobile number?"
        Optional<Employee> employeeOpt = employeeDao.findByMobileNo(mobileNo);
        if (employeeOpt.isPresent()) {
            Employee emp = employeeOpt.get(); // We found an employee!
            // We convert the Employee into our CustomUserDetails (The Passport)
            return new CustomUserDetails(emp.getMobileNo(), emp.getPassword(), emp.getRole(), emp.getIsActive());
        }

        // 2. If it wasn't an employee, ask the User table: "Do you have this mobile number?"
        Optional<UserMaster> userOpt = userMasterDao.findByMobileNo(mobileNo);
        if (userOpt.isPresent()) {
            UserMaster user = userOpt.get(); // We found a normal user!
            // We convert the User into our CustomUserDetails (The Passport)
            return new CustomUserDetails(user.getMobileNo(), user.getPassword(), user.getRole(), user.getIsActive());
        }

        // 3. We didn't find anyone in either table! Deny login.
        throw new UsernameNotFoundException("No one found with mobile number: " + mobileNo);
    }
}
