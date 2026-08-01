package com.mays.srm.user.service;

import com.mays.srm.core.service.GenericService;
import com.mays.srm.user.dto.reqDTO.VendorUserRequestDTO;
import com.mays.srm.user.dto.resDTO.VendorUserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VendorUserService extends GenericService<VendorUserRequestDTO, VendorUserResponseDTO, Integer> {
    List<VendorUserResponseDTO> getByVendorId(Integer vendorId);
    Page<VendorUserResponseDTO> getAll(Pageable pageable);
}
