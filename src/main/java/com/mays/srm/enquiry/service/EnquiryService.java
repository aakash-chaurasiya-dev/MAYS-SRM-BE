package com.mays.srm.enquiry.service;
import com.mays.srm.core.service.GenericService;
import com.mays.srm.enquiry.dto.request.EnquiryRequestDTO;
import com.mays.srm.enquiry.dto.resDTO.EnquiryResponseDTO;
import java.util.List;

public interface EnquiryService extends GenericService<EnquiryRequestDTO, EnquiryResponseDTO, Integer> {
    List<EnquiryResponseDTO> getAllEnquiriesOfUser(Integer userId);
}
