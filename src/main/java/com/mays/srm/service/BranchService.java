package com.mays.srm.service;

import com.mays.srm.dto.requestDTO.BranchRequestDTO;
import com.mays.srm.dto.responseDTO.BranchResponseDTO;
import com.mays.srm.entity.Branch;

import java.util.List;

public interface BranchService extends GenericService<Branch, Integer> {
    BranchResponseDTO createBranch(BranchRequestDTO requestDTO);
    BranchResponseDTO getBranchById(Integer id);
    List<BranchResponseDTO> getAllBranches();
}
