package com.mays.srm.enquiry.service;

import com.mays.srm.enquiry.dto.request.OutwardRequestDTO;
import com.mays.srm.enquiry.dto.resDTO.OutwardResponseDTO;
import java.util.List;

public interface OutwardService {
    OutwardResponseDTO createOutward(OutwardRequestDTO requestDTO);
    List<OutwardResponseDTO> getAllOutwards();
    OutwardResponseDTO getOutwardById(Integer id);
    OutwardResponseDTO getOutwardByTicketId(Integer ticketId);
    List<com.mays.srm.ticket.dto.resDTO.TicketResponseDTO> getEligibleTicketsForUser(Integer userId);
}

