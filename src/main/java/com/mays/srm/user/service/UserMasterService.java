package com.mays.srm.user.service;
import com.mays.srm.core.service.GenericService;
import com.mays.srm.user.dto.reqDTO.UserMasterRequestDTO;
import com.mays.srm.user.dto.resDTO.UserMasterResponseDTO;

import java.util.List;

public interface UserMasterService extends GenericService<UserMasterRequestDTO,UserMasterResponseDTO, Integer> {

    // These methods can still return entities if they are used internally
    UserMasterResponseDTO findByMobileNo(String mobileNo);
    UserMasterResponseDTO findByEmail(String email);
    List<UserMasterResponseDTO> findByFirstName(String firstName);
    List<UserMasterResponseDTO> findByLastName(String lastName);
    List<UserMasterResponseDTO> findByBranchName(String branchName);
    List<UserMasterResponseDTO> getByVendorId(Integer vendorId);
}
