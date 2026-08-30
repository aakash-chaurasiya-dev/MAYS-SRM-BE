package com.mays.srm.enquiry.service;

import com.mays.srm.enquiry.dto.request.InwardRequestDTO;
import com.mays.srm.enquiry.dto.resDTO.InwardResponseDTO;
import java.util.List;

public interface InwardService {
    InwardResponseDTO createInward(InwardRequestDTO requestDTO);
    List<InwardResponseDTO> getAllInwards();
    InwardResponseDTO getInwardById(Integer id);
}
