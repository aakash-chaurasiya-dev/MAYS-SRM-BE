package com.mays.srm.service;

import com.mays.srm.dto.requestDTO.UserMasterRequestDTO;
import com.mays.srm.dto.responseDTO.UserMasterResponseDTO;
import com.mays.srm.entity.UserMaster;

import java.util.List;

public interface UserMasterService extends GenericService<UserMasterRequestDTO,UserMasterResponseDTO, Integer> {

    // These methods can still return entities if they are used internally
    UserMasterResponseDTO findByMobileNo(String mobileNo);
    UserMasterResponseDTO findByEmail(String email);
    List<UserMasterResponseDTO> findByFirstName(String firstName);
    List<UserMasterResponseDTO> findByLastName(String lastName);
    List<UserMasterResponseDTO> findByBranchName(String branchName);
}
