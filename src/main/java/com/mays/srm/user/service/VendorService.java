package com.mays.srm.user.service;

import java.util.List;

import com.mays.srm.user.dto.resDTO.VendorResponseDTO;
import com.mays.srm.user.dto.reqDTO.VendorRequestDTO;
import com.mays.srm.core.service.GenericService;

public interface VendorService extends GenericService<VendorRequestDTO,VendorResponseDTO, Integer> {
     // These methods can still return entities if they are used internally
    VendorResponseDTO findByMobileNo(String mobileNo);
    VendorResponseDTO findByEmail(String email);
    List<VendorResponseDTO> findByFirstName(String firstName);
    List<VendorResponseDTO> findByLastName(String lastName);
    List<VendorResponseDTO> findByBranchName(String branchName);
}
