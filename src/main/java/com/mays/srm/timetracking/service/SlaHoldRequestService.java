package com.mays.srm.timetracking.service;

import java.util.List;

import com.mays.srm.timetracking.dto.resDTO.SlaHoldRequestResponseDTO;

public interface SlaHoldRequestService {

    List<SlaHoldRequestResponseDTO> getPendingRequests();

    List<SlaHoldRequestResponseDTO> getByTicketId(Integer ticketId);

    SlaHoldRequestResponseDTO getActiveForTicket(Integer ticketId);
}
