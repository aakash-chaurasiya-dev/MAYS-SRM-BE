package com.mays.srm.security.service;

import com.mays.srm.security.entities.SecurityProfile;
import com.mays.srm.security.repository.SecurityProfileDao;
import com.mays.srm.user.entities.Employee;
import com.mays.srm.user.entities.UserMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SecurityProfileService {

    @Autowired
    private SecurityProfileDao securityProfileDao;

    public SecurityProfile getOrCreateProfileForUser(UserMaster user) {
        Optional<SecurityProfile> profileOpt = securityProfileDao.findByUser_UserId(user.getUserId());
        if (profileOpt.isPresent()) {
            return profileOpt.get();
        }
        SecurityProfile profile = new SecurityProfile();
        profile.setUser(user);
        return securityProfileDao.save(profile);
    }

    public SecurityProfile getOrCreateProfileForEmployee(Employee employee) {
        Optional<SecurityProfile> profileOpt = securityProfileDao.findByEmployee_EmployeeId(employee.getEmployeeId());
        if (profileOpt.isPresent()) {
            return profileOpt.get();
        }
        SecurityProfile profile = new SecurityProfile();
        profile.setEmployee(employee);
        return securityProfileDao.save(profile);
    }

    public void recordFailedAttempt(SecurityProfile profile) {
        profile.setNoOfFailedAttempts(profile.getNoOfFailedAttempts() + 1);
        if (profile.getNoOfFailedAttempts() >= 5) {
            profile.setAccountLockedUntil(LocalDateTime.now().plusHours(2));
        }
        securityProfileDao.save(profile);
    }

    public void resetFailedAttempts(SecurityProfile profile) {
        if (profile.getNoOfFailedAttempts() > 0 || profile.getAccountLockedUntil() != null) {
            profile.setNoOfFailedAttempts(0);
            profile.setAccountLockedUntil(null);
            securityProfileDao.save(profile);
        }
    }

    public boolean isAccountLocked(SecurityProfile profile) {
        if (profile.getAccountLockedUntil() == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(profile.getAccountLockedUntil())) {
            // Lock expired, reset it
            profile.setAccountLockedUntil(null);
            profile.setNoOfFailedAttempts(0);
            securityProfileDao.save(profile);
            return false;
        }
        return true;
    }
}
