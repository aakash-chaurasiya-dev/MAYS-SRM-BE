package com.mays.srm.service.impl;

import com.mays.srm.dao.core.BranchDao;
import com.mays.srm.dao.core.UserMasterDao;
import com.mays.srm.entity.Branch;
import com.mays.srm.entity.UserMaster;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.UserMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserMasterServiceImpl implements UserMasterService {

    private final UserMasterDao repository;
    private final BranchDao branchDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserMasterServiceImpl(UserMasterDao repository, BranchDao branchDao, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.branchDao = branchDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserMaster create(UserMaster entity) {
        try {
            // Check if branch needs to be fetched and linked
            if (entity.getBranch() != null && entity.getBranch().getBranchId() != null) {
                Optional<Branch> branchOpt = branchDao.findById(entity.getBranch().getBranchId());
                
                if (branchOpt.isPresent()) {
                    entity.setBranch(branchOpt.get());
                } else {
                    throw new ResourceNotFoundException("Branch not found with ID: " + entity.getBranch().getBranchId());
                }
            }

            // Encode password before saving
            if (entity.getPassword() != null) {
                entity.setPassword(passwordEncoder.encode(entity.getPassword()));
            }

            return repository.save(entity);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex; // Re-throw specifically so GlobalExceptionHandler catches them properly
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating User", ex);
        }
    }

    @Override
    public Optional<UserMaster> getById(String id) {
        Optional<UserMaster> user = repository.findById(id);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found with ID (mobileNo): " + id);
        }
        return user;
    }

    @Override
    public List<UserMaster> getAll() {
        return repository.findAll();
    }

    @Override
    public UserMaster update(UserMaster entity) {
        try {
            if (entity.getUserId() == null || !repository.existsById(entity.getUserId())) {
                throw new ResourceNotFoundException("Cannot update. User not found with ID: " + entity.getUserId());
            }

            // Handle branch linking
            if (entity.getBranch() != null && entity.getBranch().getBranchId() != null) {
                Optional<Branch> branchOpt = branchDao.findById(entity.getBranch().getBranchId());
                
                if (branchOpt.isPresent()) {
                    entity.setBranch(branchOpt.get());
                } else {
                    throw new ResourceNotFoundException("Branch not found with ID: " + entity.getBranch().getBranchId());
                }
            }

            // Encode password if it is being changed
            if (entity.getPassword() != null && !entity.getPassword().startsWith("$2a$")) {
                entity.setPassword(passwordEncoder.encode(entity.getPassword()));
            }

            return repository.save(entity);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating User", ex);
        }
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. User not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete User because they are currently assigned to active tickets or enquiries.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting User with ID: " + id, ex);
        }
    }

    @Override
    public UserMaster findByMobileNo(String mobileNo) {
        Optional<UserMaster> userOpt = repository.findByMobileNo(mobileNo);
        
        if (userOpt.isPresent()) {
            return userOpt.get();
        } else {
            throw new ResourceNotFoundException("User not found with mobile number: " + mobileNo);
        }
    }

    @Override
    public UserMaster findByEmail(String email) {
        Optional<UserMaster> userOpt = repository.findByEmailId(email);
        
        if (userOpt.isPresent()) {
            return userOpt.get();
        } else {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }
    }

    @Override
    public List<UserMaster> findByFirstName(String firstName) {
        List<UserMaster> users = repository.findByFirstName(firstName);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found with first name: " + firstName);
        }
        return users;
    }

    @Override
    public List<UserMaster> findByLastName(String lastName) {
        List<UserMaster> users = repository.findByLastName(lastName);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found with last name: " + lastName);
        }
        return users;
    }

    @Override
    public List<UserMaster> findByBranchName(String branchName) {
        List<UserMaster> users = repository.findByBranchBranchName(branchName);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found in branch: " + branchName);
        }
        return users;
    }
}
