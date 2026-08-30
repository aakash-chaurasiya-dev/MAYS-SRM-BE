package com.mays.srm.enquiry.service;
import com.mays.srm.core.service.GenericService;
import com.mays.srm.enquiry.dto.request.EnquiryRequestDTO;
import com.mays.srm.enquiry.dto.resDTO.EnquiryPendingCountDTO;
import com.mays.srm.enquiry.dto.resDTO.EnquiryResponseDTO;
import com.mays.srm.ticket.dto.resDTO.TicketResponseDTO;
import java.util.List;

public interface EnquiryService extends GenericService<EnquiryRequestDTO, EnquiryResponseDTO, Integer> {
    List<EnquiryResponseDTO> getAllEnquiriesOfUser(Integer userId);
    EnquiryPendingCountDTO getPendingCountAll();
    EnquiryPendingCountDTO getPendingCountForUser(Integer userId);
    TicketResponseDTO convertToTicket(Integer enquiryId, Integer employeeId);
}

