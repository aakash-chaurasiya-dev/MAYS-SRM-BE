package com.mays.srm.organization.service.impl;
import com.mays.srm.organization.repository.BranchDao;
import com.mays.srm.organization.dto.request.BranchRequestDTO;
import com.mays.srm.organization.dto.resDTO.BranchResponseDTO;
import com.mays.srm.organization.entities.Branch;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.organization.service.BranchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public BranchResponseDTO create(BranchRequestDTO requestDTO) {
        try {
            Branch branch = modelMapper.map(requestDTO, Branch.class);
            Branch savedBranch = repository.save(branch);
            return modelMapper.map(savedBranch, BranchResponseDTO.class);
        } catch (DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Branch", ex);
        }
    }

    @Override
    public BranchResponseDTO getById(Integer id) {
        Branch branch = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + id));
        return modelMapper.map(branch, BranchResponseDTO.class);
    }

    @Override
    public List<BranchResponseDTO> getAll() {
        return repository.findAll().stream()
                .map(branch -> modelMapper.map(branch, BranchResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public BranchResponseDTO update(Integer id, BranchRequestDTO requestDTO) {
        Branch existingBranch = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot update. Branch not found with ID: " + id));

        modelMapper.map(requestDTO, existingBranch);
        existingBranch.setBranchId(id);

        try {
            Branch updatedBranch = repository.save(existingBranch);
            return modelMapper.map(updatedBranch, BranchResponseDTO.class);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Branch", ex);
        }
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

