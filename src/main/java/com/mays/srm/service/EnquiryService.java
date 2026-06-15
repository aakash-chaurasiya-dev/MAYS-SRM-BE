package com.mays.srm.service;

import com.mays.srm.dto.requestDTO.EnquiryRequestDTO;
import com.mays.srm.dto.responseDTO.EnquiryResponseDTO;
import java.util.List;

public interface EnquiryService extends GenericService<EnquiryRequestDTO, EnquiryResponseDTO, Integer> {
    List<EnquiryResponseDTO> getAllEnquiriesOfUser(Integer userId);
}
