package com.mays.srm.service;

import com.mays.srm.dto.requestDTO.BranchRequestDTO;
import com.mays.srm.dto.responseDTO.BranchResponseDTO;

public interface BranchService extends GenericService<BranchRequestDTO, BranchResponseDTO, Integer> {
    // Saare standard methods (create, getById, getAll, update, delete) 
    // automatically GenericService se mil jayenge, aur wo DTOs use karenge!
}
