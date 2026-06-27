package com.mays.srm.organization.service;
import com.mays.srm.service.GenericService;
import com.mays.srm.organization.dto.request.BranchRequestDTO;
import com.mays.srm.organization.dto.resDTO.BranchResponseDTO;

public interface BranchService extends GenericService<BranchRequestDTO, BranchResponseDTO, Integer> {
    // Saare standard methods (create, getById, getAll, update, delete) 
    // automatically GenericService se mil jayenge, aur wo DTOs use karenge!
}
