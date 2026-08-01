package com.mays.srm.user.service.impl;

import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.user.dto.reqDTO.VendorRequestDTO;
import com.mays.srm.user.dto.resDTO.VendorResponseDTO;
import com.mays.srm.user.entities.Vendor;
import com.mays.srm.user.repository.VendorDao;
import com.mays.srm.user.service.EmployeeService;
import com.mays.srm.user.service.VendorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.mays.srm.util.RestPageImpl;

@Service
public class VendorServiceImpl implements VendorService {

    private final VendorDao repository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final EmployeeService employeeService;

    @Autowired
    public VendorServiceImpl(VendorDao repository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, EmployeeService employeeService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.employeeService = employeeService;
    }

    @Override
    public VendorResponseDTO create(VendorRequestDTO requestDTO) {
        try {
            // tells us if this mobile no is registered with any employee, user or vendor
            employeeService.validateMobileNumber(requestDTO.getMobileNo(), null, null, null);
            Vendor vendor = modelMapper.map(requestDTO, Vendor.class);
            
            // Encode password
            if (vendor.getPassword() != null) {
                vendor.setPassword(passwordEncoder.encode(vendor.getPassword()));
            } else {
                throw new BadRequestException("Password is required for creating a new vendor.");
            }
            
            // Set default role if not provided
            if (vendor.getRoleName() == null) {
                vendor.setRoleName("ROLE_VENDOR");
            }

            if (vendor.getIsActive() == null) {
                vendor.setIsActive(true);
            }

            Vendor savedVendor = repository.save(vendor);
            return modelMapper.map(savedVendor, VendorResponseDTO.class);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Vendor", ex);
        }
    }

    @Override
    public VendorResponseDTO getById(Integer id) {
        Vendor vendor = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + id));
        return modelMapper.map(vendor, VendorResponseDTO.class);
    }

    @Override
    public List<VendorResponseDTO> getAll() {
        return repository.findAll().stream()
                .map(vendor -> modelMapper.map(vendor, VendorResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public VendorResponseDTO update(Integer id, VendorRequestDTO requestDTO) {
        try {
            Optional<Vendor> existingVendorOpt = repository.findById(id);
            if (existingVendorOpt.isEmpty()) {
                throw new ResourceNotFoundException("Cannot update. Vendor not found with ID: " + id);
            }
            
            employeeService.validateMobileNumber(requestDTO.getMobileNo(), null, null, id);
            
            Vendor existingVendor = existingVendorOpt.get();
            String currentPassword = existingVendor.getPassword();
            Boolean currentIsActive = existingVendor.getIsActive();
            modelMapper.map(requestDTO, existingVendor);

            // Preserve original password if not provided in the request
            if (requestDTO.getPassword() == null || requestDTO.getPassword().isEmpty()) {
                existingVendor.setPassword(currentPassword);
            } else if (!requestDTO.getPassword().startsWith("$2a$")) { // Only re-encode if it's a new plain text password
                existingVendor.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
            }

            // Restore or set isActive
            if (currentIsActive != null) {
                existingVendor.setIsActive(currentIsActive);
            }

            Vendor updatedVendor = repository.save(existingVendor);
            return modelMapper.map(updatedVendor, VendorResponseDTO.class);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Vendor", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Vendor not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Vendor because they are assigned to active records.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Vendor with ID: " + id, ex);
        }
    }

    @Override
    public VendorResponseDTO findByMobileNo(String mobileNo) {
        Vendor vendor = repository.findByMobileNo(mobileNo)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with mobile number: " + mobileNo));
        return modelMapper.map(vendor, VendorResponseDTO.class);
    }

    @Override
    public VendorResponseDTO findByEmail(String email) {
        Vendor vendor = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with email: " + email));
        return modelMapper.map(vendor, VendorResponseDTO.class);
    }

    // The following methods are not supported by the current DAO and would require custom queries.
    // I will throw UnsupportedOperationException as a placeholder.

    @Override
    public List<VendorResponseDTO> findByFirstName(String firstName) {
        throw new UnsupportedOperationException("Finding vendors by first name is not yet implemented.");
    }

    @Override
    public List<VendorResponseDTO> findByLastName(String lastName) {
        throw new UnsupportedOperationException("Finding vendors by last name is not yet implemented.");
    }

    @Override
    public List<VendorResponseDTO> findByBranchName(String branchName) {
        throw new UnsupportedOperationException("Finding vendors by branch name is not yet implemented.");
    }
    @Override
    public Page<VendorResponseDTO> getPaginated(Pageable pageable) {
        Page<Vendor> vendorPage = repository.findAll(pageable);
        List<VendorResponseDTO> dtoList = vendorPage.stream()
                .map(vendor -> modelMapper.map(vendor, VendorResponseDTO.class))
                .collect(Collectors.toList());
        return new RestPageImpl<>(dtoList, pageable, vendorPage.getTotalElements());
    }
}
