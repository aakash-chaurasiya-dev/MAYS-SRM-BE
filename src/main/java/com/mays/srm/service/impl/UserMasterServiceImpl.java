package com.mays.srm.service.impl;

import com.mays.srm.dao.core.BranchDao;
import com.mays.srm.dao.core.UserMasterDao;
import com.mays.srm.dto.requestDTO.UserMasterRequestDTO;
import com.mays.srm.dto.responseDTO.UserMasterResponseDTO;
import com.mays.srm.entity.Branch;
import com.mays.srm.entity.UserMaster;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.EmployeeService;
import com.mays.srm.service.UserMasterService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserMasterServiceImpl implements UserMasterService {

    private final UserMasterDao repository;
    private final BranchDao branchDao;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final EmployeeService employeeService;

    @Autowired
    public UserMasterServiceImpl(UserMasterDao repository, BranchDao branchDao, PasswordEncoder passwordEncoder, ModelMapper modelMapper, EmployeeService employeeService) {
        this.repository = repository;
        this.branchDao = branchDao;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.employeeService = employeeService;
    }

    @Override
    public UserMasterResponseDTO create(UserMasterRequestDTO requestDTO) {
        try {
            employeeService.validateMobileNumber(requestDTO.getMobileNo(), null, null);
            UserMaster user = modelMapper.map(requestDTO, UserMaster.class);
            
            if (requestDTO.getBranchId() != null) {
                Optional<Branch> branchOpt = branchDao.findById(requestDTO.getBranchId());
                if (branchOpt.isPresent()) {
                    user.setBranch(branchOpt.get());
                } else {
                    throw new ResourceNotFoundException("Branch not found with ID: " + requestDTO.getBranchId());
                }
            }

            if (user.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            UserMaster savedUser = repository.save(user);
            return mapToResponseDTO(savedUser);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating User", ex);
        }
    }

    @Override
    public UserMasterResponseDTO getById(Integer id) {
        Optional<UserMaster> userOpt = repository.findById(id);
        if (userOpt.isPresent()) {
            return mapToResponseDTO(userOpt.get());
        } else {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
    }

    @Override
    public List<UserMasterResponseDTO> getAll() {
        List<UserMaster> userList = repository.findAll();
        List<UserMasterResponseDTO> dtoList = new ArrayList<>();
        for (UserMaster user : userList) {
            dtoList.add(mapToResponseDTO(user));
        }
        return dtoList;
    }

    @Override
    public UserMasterResponseDTO update(Integer id, UserMasterRequestDTO requestDTO) {
        try {
            Optional<UserMaster> existingUserOpt = repository.findById(id);
            if (existingUserOpt.isEmpty()) {
                throw new ResourceNotFoundException("Cannot update. User not found with ID: " + id);
            }
            
            employeeService.validateMobileNumber(requestDTO.getMobileNo(), null, id);

            UserMaster existingUser = existingUserOpt.get();
            String currentPassword = existingUser.getPassword();
            Boolean currentIsActive = existingUser.getIsActive();
            modelMapper.map(requestDTO, existingUser);
            
            if (requestDTO.getPassword() == null || requestDTO.getPassword().isEmpty()) {
                existingUser.setPassword(currentPassword);
            } else if (!requestDTO.getPassword().startsWith("$2a$")) {
                existingUser.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
            }

            if (requestDTO.getIsActive() == null) {
                existingUser.setIsActive(currentIsActive);
            }

            if (requestDTO.getBranchId() != null) {
                Optional<Branch> branchOpt = branchDao.findById(requestDTO.getBranchId());
                if (branchOpt.isPresent()) {
                    existingUser.setBranch(branchOpt.get());
                } else {
                    throw new ResourceNotFoundException("Branch not found with ID: " + requestDTO.getBranchId());
                }
            } else {
                existingUser.setBranch(null);
            }

            UserMaster updatedUser = repository.save(existingUser);
            return mapToResponseDTO(updatedUser);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating User", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. User not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete User because they are assigned to active records.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting User with ID: " + id, ex);
        }
    }

    // --- Custom Find Methods ---

    @Override
    public UserMasterResponseDTO findByMobileNo(String mobileNo) {
        Optional<UserMaster> userOpt = repository.findByMobileNo(mobileNo);
        if (userOpt.isPresent()) {
            return mapToResponseDTO(userOpt.get());
        } else {
            throw new ResourceNotFoundException("User not found with mobile number: " + mobileNo);
        }
    }

    @Override
    public UserMasterResponseDTO findByEmail(String email) {
        Optional<UserMaster> userOpt = repository.findByEmailId(email);
        if (userOpt.isPresent()) {
            return mapToResponseDTO(userOpt.get());
        } else {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }
    }

    @Override
    public List<UserMasterResponseDTO> findByFirstName(String firstName) {
        List<UserMaster> userList = repository.findByFirstName(firstName);
        List<UserMasterResponseDTO> dtoList = new ArrayList<>();
        for (UserMaster user : userList) {
            dtoList.add(mapToResponseDTO(user));
        }
        return dtoList;
    }

    @Override
    public List<UserMasterResponseDTO> findByLastName(String lastName) {
        List<UserMaster> userList = repository.findByLastName(lastName);
        List<UserMasterResponseDTO> dtoList = new ArrayList<>();
        for (UserMaster user : userList) {
            dtoList.add(mapToResponseDTO(user));
        }
        return dtoList;
    }

    @Override
    public List<UserMasterResponseDTO> findByBranchName(String branchName) {
        throw new UnsupportedOperationException("Finding users by branch name is not yet implemented.");
    }

    // --- Helper Methods ---

    private UserMasterResponseDTO mapToResponseDTO(UserMaster user) {
        UserMasterResponseDTO dto = modelMapper.map(user, UserMasterResponseDTO.class);
        if (user.getBranch() != null) {
            dto.setBranchName(user.getBranch().getBranchName());
        }
        return dto;
    }
}
