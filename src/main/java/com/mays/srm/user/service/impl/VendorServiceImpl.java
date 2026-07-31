package com.mays.srm.user.service.impl;

import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.organization.repository.BranchDao;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            //tells us if this mobile no is registed with any employee, user or vendor
            employeeService.validateMobileNumber(requestDTO.getMobileNo(), null, null,null);
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

            Vendor savedVendor = repository.save(vendor);
            return modelMapper.map(savedVendor, VendorResponseDTO.class);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("A vendor with this email or mobile number already exists.", ex);
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
            Optional<Vendor> existingVendor = repository.findById(id);
            if(existingVendor.isEmpty()){
                throw new ResourceNotFoundException("Cannot update. Vendor not found with ID: " + id);
            }
            employeeService.validateMobileNumber(requestDTO.getMobileNo(), null, null, id);

            String currentPassword = existingVendor.getPassword();
            modelMapper.map(requestDTO, existingVendor);

            // Preserve original password if not provided in the request
            if (requestDTO.getPassword() == null || requestDTO.getPassword().isEmpty()) {
                existingVendor.setPassword(currentPassword);
            } else if (!requestDTO.getPassword().startsWith("$2a$")) { // Only re-encode if it's a new plain text password
                existingVendor.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
            }


            try {
                Vendor updatedVendor = repository.save(existingVendor);
                return modelMapper.map(updatedVendor, VendorResponseDTO.class);
            } catch (DataIntegrityViolationException ex) {
                throw new BadRequestException("A vendor with this email or mobile number already exists.", ex);
            } catch (Exception ex) {
                throw new InternalServerException("Error occurred while updating Vendor", ex);
            }
        }
        catch (ResourceNotFoundException ex) {
            throw ex;
        }
        catch (Exception ex) {
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
            throw new BadRequestException("Cannot delete Vendor because they are assigned to active records.", ex);
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
}
