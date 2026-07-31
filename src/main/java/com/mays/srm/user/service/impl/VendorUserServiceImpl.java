package com.mays.srm.user.service.impl;

import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.user.dto.reqDTO.VendorUserRequestDTO;
import com.mays.srm.user.dto.resDTO.VendorUserResponseDTO;
import com.mays.srm.user.entities.Vendor;
import com.mays.srm.user.entities.VendorUser;
import com.mays.srm.user.repository.VendorDao;
import com.mays.srm.user.repository.VendorUserDao;
import com.mays.srm.user.service.VendorUserService;
import com.mays.srm.util.RestPageImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VendorUserServiceImpl implements VendorUserService {

    private final VendorUserDao repository;
    private final VendorDao vendorDao;
    private final ModelMapper modelMapper;

    @Autowired
    public VendorUserServiceImpl(VendorUserDao repository, VendorDao vendorDao, ModelMapper modelMapper) {
        this.repository = repository;
        this.vendorDao = vendorDao;
        this.modelMapper = modelMapper;
    }

    @Override
    @CacheEvict(value = {"vendorUsers", "vendorUsersByVendorId"}, allEntries = true)
    public VendorUserResponseDTO create(VendorUserRequestDTO requestDTO) {
        try {
            Vendor vendor = vendorDao.findById(requestDTO.getVendorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + requestDTO.getVendorId()));

            VendorUser vendorUser = modelMapper.map(requestDTO, VendorUser.class);
            vendorUser.setVendor(vendor);

            if (vendorUser.getIsActive() == null) {
                vendorUser.setIsActive(true);
            }

            VendorUser saved = repository.save(vendorUser);
            return mapToResponseDTO(saved);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating VendorUser", ex);
        }
    }

    @Override
    @Cacheable(value = "vendorUserSingle", key = "#id")
    public VendorUserResponseDTO getById(Integer id) {
        VendorUser vendorUser = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VendorUser not found with ID: " + id));
        return mapToResponseDTO(vendorUser);
    }

    @Override
    public List<VendorUserResponseDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "vendorUsers", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<VendorUserResponseDTO> getAll(Pageable pageable) {
        Page<VendorUser> page = repository.findAll(pageable);
        List<VendorUserResponseDTO> dtoList = page.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return new RestPageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "vendorUserSingle", key = "#id"),
        @CacheEvict(value = {"vendorUsers", "vendorUsersByVendorId"}, allEntries = true)
    })
    public VendorUserResponseDTO update(Integer id, VendorUserRequestDTO requestDTO) {
        try {
            VendorUser existing = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Cannot update. VendorUser not found with ID: " + id));

                    
            Vendor vendor = vendorDao.findById(requestDTO.getVendorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + requestDTO.getVendorId()));

            existing.setUser(requestDTO.getUser());
            existing.setContactNo(requestDTO.getContactNo());
            existing.setIsActive(requestDTO.getIsActive() != null ? requestDTO.getIsActive() : existing.getIsActive());
            existing.setVendor(vendor);

            VendorUser updated = repository.save(existing);
            return mapToResponseDTO(updated);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating VendorUser with ID: " + id, ex);
        }
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "vendorUserSingle", key = "#id"),
        @CacheEvict(value = {"vendorUsers", "vendorUsersByVendorId"}, allEntries = true)
    })
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. VendorUser not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete VendorUser because it is linked to other active records.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting VendorUser with ID: " + id, ex);
        }
    }

    @Override
    @Cacheable(value = "vendorUsersByVendorId", key = "#vendorId")
    public List<VendorUserResponseDTO> getByVendorId(Integer vendorId) {
        if (!vendorDao.existsById(vendorId)) {
            throw new ResourceNotFoundException("Vendor not found with ID: " + vendorId);
        }
        return repository.findByVendorId(vendorId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private VendorUserResponseDTO mapToResponseDTO(VendorUser vendorUser) {
        VendorUserResponseDTO response = modelMapper.map(vendorUser, VendorUserResponseDTO.class);
        if (vendorUser.getVendor() != null) {
            response.setVendorId(vendorUser.getVendor().getId());
            response.setVendorName(vendorUser.getVendor().getName());
        }
        return response;
    }
}
