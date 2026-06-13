package com.mays.srm.service;

import com.mays.srm.dto.requestDTO.BillingRequestDTO;
import com.mays.srm.dto.responseDTO.BillingResponseDTO;

import java.util.List;

public interface BillingService extends GenericService<BillingRequestDTO, BillingResponseDTO, Integer> {
    List<BillingResponseDTO> getFinalCharges();
    List<BillingResponseDTO> getChargesByTicketId(Integer ticketId);
    void bulkUpdateCharges(Integer ticketId, List<BillingRequestDTO> charges);
}
