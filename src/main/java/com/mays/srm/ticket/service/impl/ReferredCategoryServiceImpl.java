package com.mays.srm.ticket.service.impl;

import com.mays.srm.ticket.repository.ReferredCategoryDao;
import com.mays.srm.ticket.dto.request.ReferredCategoryRequestDTO;
import com.mays.srm.ticket.dto.resDTO.ReferredCategoryResponseDTO;
import com.mays.srm.ticket.entities.ReferredCategory;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.ticket.service.ReferredCategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReferredCategoryServiceImpl implements ReferredCategoryService {

    private final ReferredCategoryDao repository;
    private final ModelMapper modelMapper;

    @Autowired
    public ReferredCategoryServiceImpl(ReferredCategoryDao repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    @CacheEvict(value = "referredCategories", allEntries = true)
    public ReferredCategoryResponseDTO create(ReferredCategoryRequestDTO requestDTO) {
        try {
            ReferredCategory category = modelMapper.map(requestDTO, ReferredCategory.class);
            ReferredCategory savedCategory = repository.save(category);
            return modelMapper.map(savedCategory, ReferredCategoryResponseDTO.class);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Referred Category", ex);
        }
    }

    @Override
    @Cacheable(value = "referredCategories", key = "#id")
    public ReferredCategoryResponseDTO getById(Integer id) {
        Optional<ReferredCategory> categoryOpt = repository.findById(id);
        if (categoryOpt.isPresent()) {
            return modelMapper.map(categoryOpt.get(), ReferredCategoryResponseDTO.class);
        } else {
            throw new ResourceNotFoundException("Referred Category not found with ID: " + id);
        }
    }

    @Override
    @Cacheable(value = "referredCategories", key = "'all'")
    public List<ReferredCategoryResponseDTO> getAll() {
        List<ReferredCategory> categoryList = repository.findAll();
        List<ReferredCategoryResponseDTO> dtoList = new ArrayList<>();
        for (ReferredCategory category : categoryList) {
            dtoList.add(modelMapper.map(category, ReferredCategoryResponseDTO.class));
        }
        return dtoList;
    }

    @Override
    @CacheEvict(value = "referredCategories", allEntries = true)
    public ReferredCategoryResponseDTO update(Integer id, ReferredCategoryRequestDTO requestDTO) {
        Optional<ReferredCategory> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Referred Category not found with ID: " + id);
        }
        
        ReferredCategory existingCategory = existingOpt.get();
        Boolean originalLocked = existingCategory.getIsLocked();
        modelMapper.map(requestDTO, existingCategory);
        if (requestDTO.getIsLocked() == null) {
            existingCategory.setIsLocked(originalLocked);
        }
        
        try {
            ReferredCategory updatedCategory = repository.save(existingCategory);
            return modelMapper.map(updatedCategory, ReferredCategoryResponseDTO.class);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Referred Category", ex);
        }
    }

    @Override
    @CacheEvict(value = "referredCategories", allEntries = true)
    public void delete(Integer id) {
        Optional<ReferredCategory> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot delete. Referred Category not found with ID: " + id);
        }
        if (Boolean.TRUE.equals(existingOpt.get().getIsLocked())) {
            throw new IllegalArgumentException("Referred Category is locked and cannot be deleted.");
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Referred Category because it is currently in use.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Referred Category with ID: " + id, ex);
        }
    }
}
