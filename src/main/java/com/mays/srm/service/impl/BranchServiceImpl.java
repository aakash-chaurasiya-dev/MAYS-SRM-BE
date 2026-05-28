package com.mays.srm.service.impl;

import com.mays.srm.dao.core.BranchDao;
import com.mays.srm.dto.requestDTO.BranchRequestDTO;
import com.mays.srm.dto.responseDTO.BranchResponseDTO;
import com.mays.srm.entity.Branch;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.BranchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchDao repository;
    private final ModelMapper modelMapper;

    @Autowired
    public BranchServiceImpl(BranchDao repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BranchResponseDTO createBranch(BranchRequestDTO requestDTO) {
        // Map DTO to Entity
        Branch branch = modelMapper.map(requestDTO, Branch.class);
        
        // Save the Entity
        Branch savedBranch = create(branch); // Use the existing generic create method
        
        // Map Entity back to Response DTO
        return modelMapper.map(savedBranch, BranchResponseDTO.class);
    }

    @Override
    public BranchResponseDTO getBranchById(Integer id) {
        Branch branch = getById(id).orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + id));
        return modelMapper.map(branch, BranchResponseDTO.class);
    }

    @Override
    public List<BranchResponseDTO> getAllBranches() {
        return getAll().stream()
                .map(branch -> modelMapper.map(branch, BranchResponseDTO.class))
                .collect(Collectors.toList());
    }

    // --- GenericService Methods Implementation ---

    @Override
    public Branch create(Branch entity) {
        try {
            return repository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Branch", ex);
        }
    }

    @Override
    public Optional<Branch> getById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<Branch> getAll() {
        return repository.findAll();
    }

    @Override
    public Branch update(Branch entity) {
        if (entity.getBranchId() == null || !repository.existsById(entity.getBranchId())) {
            throw new ResourceNotFoundException("Cannot update. Branch not found with ID: " + entity.getBranchId());
        }
        return repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Branch not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
             throw new DataIntegrityViolationException("Cannot delete branch because it is currently assigned to a user or ticket.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Branch with ID: " + id, ex);
        }
    }
}
