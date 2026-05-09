package com.mays.srm.service.impl;

import com.mays.srm.dao.core.BranchDao;
import com.mays.srm.entity.Branch;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchDao repository;

    @Autowired
    public BranchServiceImpl(BranchDao repository) {
        this.repository = repository;
    }

    @Override
    public Branch create(Branch entity) {
        try {
            return repository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            throw ex; // Re-throw to be handled by GlobalExceptionHandler as 409 Conflict
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Branch", ex);
        }
    }

    @Override
    public Optional<Branch> getById(Integer id) {
        Optional<Branch> branch = repository.findById(id);
        if (branch.isEmpty()) {
            throw new ResourceNotFoundException("Branch not found with ID: " + id);
        }
        return branch;
    }

    @Override
    public List<Branch> getAll() {
        return repository.findAll();
    }

    @Override
    public Branch update(Branch entity) {
        try {
            // Check if it exists before updating
            if (entity.getBranchId() == null || !repository.existsById(entity.getBranchId())) {
                throw new ResourceNotFoundException("Cannot update. Branch not found with ID: " + entity.getBranchId());
            }
            return repository.save(entity);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
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
             // Will trigger if another table (like Ticket or UserMaster) relies on this branch
             throw new DataIntegrityViolationException("Cannot delete branch because it is currently assigned to a user or ticket.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Branch with ID: " + id, ex);
        }
    }
}
