package com.mays.srm.security.service;

import com.mays.srm.security.entities.SecurityProfile;
import com.mays.srm.security.repository.SecurityProfileDao;
import com.mays.srm.user.entities.Employee;
import com.mays.srm.user.entities.UserMaster;
import com.mays.srm.user.entities.Vendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SecurityProfileService {

    @Autowired
    private SecurityProfileDao securityProfileDao;

    @Transactional
    public SecurityProfile getOrCreateProfileForUser(UserMaster user) {
        Optional<SecurityProfile> profileOpt = securityProfileDao.findByUser_UserId(user.getUserId());
        if (profileOpt.isPresent()) {
            return profileOpt.get();
        }
        SecurityProfile profile = new SecurityProfile();
        profile.setUser(user);
        try {
            return securityProfileDao.save(profile);
        } catch (DataIntegrityViolationException e) {
            // Another thread created it a microsecond ago — fetch the winner.
            return securityProfileDao.findByUser_UserId(user.getUserId())
                    .orElseThrow(() -> new IllegalStateException("SecurityProfile vanished after race", e));
        }
    }

    @Transactional
    public SecurityProfile getOrCreateProfileForEmployee(Employee employee) {
        Optional<SecurityProfile> profileOpt = securityProfileDao.findByEmployee_EmployeeId(employee.getEmployeeId());
        if (profileOpt.isPresent()) {
            return profileOpt.get();
        }
        SecurityProfile profile = new SecurityProfile();
        profile.setEmployee(employee);
        try {
            return securityProfileDao.save(profile);
        } catch (DataIntegrityViolationException e) {
            return securityProfileDao.findByEmployee_EmployeeId(employee.getEmployeeId())
                    .orElseThrow(() -> new IllegalStateException("SecurityProfile vanished after race", e));
        }
    }

    @Transactional
    public SecurityProfile getOrCreateProfileForVendor(Vendor vendor) {
        Optional<SecurityProfile> profileOpt = securityProfileDao.findByVendor_Id(vendor.getId());
        if (profileOpt.isPresent()) {
            return profileOpt.get();
        }
        SecurityProfile profile = new SecurityProfile();
        profile.setVendor(vendor);
        try {
            return securityProfileDao.save(profile);
        } catch (DataIntegrityViolationException e) {
            return securityProfileDao.findByVendor_Id(vendor.getId())
                    .orElseThrow(() -> new IllegalStateException("SecurityProfile vanished after race", e));
        }
    }

    @Transactional
    public void recordFailedAttempt(SecurityProfile profile) {
        profile.setNoOfFailedAttempts(profile.getNoOfFailedAttempts() + 1);
        if (profile.getNoOfFailedAttempts() >= 5) {
            profile.setAccountLockedUntil(LocalDateTime.now().plusHours(2));
        }
        securityProfileDao.save(profile);
    }

    @Transactional
    public void resetFailedAttempts(SecurityProfile profile) {
        if (profile.getNoOfFailedAttempts() > 0 || profile.getAccountLockedUntil() != null) {
            profile.setNoOfFailedAttempts(0);
            profile.setAccountLockedUntil(null);
            securityProfileDao.save(profile);
        }
    }

    @Transactional
    public void markPasswordChanged(SecurityProfile profile) {
        profile.setFirstTimeLogin(false);
        profile.setLastPassUpdate(LocalDateTime.now());
        profile.setTokenVersion(profile.getTokenVersion() == null ? 1 : profile.getTokenVersion() + 1);
        securityProfileDao.save(profile);
    }

    @Transactional
    public boolean isAccountLocked(SecurityProfile profile) {
        if (profile.getAccountLockedUntil() == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(profile.getAccountLockedUntil())) {
            profile.setAccountLockedUntil(null);
            profile.setNoOfFailedAttempts(0);
            securityProfileDao.save(profile);
            return false;
        }
        return true;
    }
}