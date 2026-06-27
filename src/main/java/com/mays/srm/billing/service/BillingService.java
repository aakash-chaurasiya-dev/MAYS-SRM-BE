package com.mays.srm.billing.service;
import com.mays.srm.service.GenericService;
import com.mays.srm.billing.dto.request.BillingRequestDTO;
import com.mays.srm.billing.dto.resDTO.BillingResponseDTO;

import java.util.List;

public interface BillingService extends GenericService<BillingRequestDTO, BillingResponseDTO, Integer> {
    List<BillingResponseDTO> getFinalCharges();
    List<BillingResponseDTO> getChargesByTicketId(Integer ticketId);
    void bulkUpdateCharges(Integer ticketId, List<BillingRequestDTO> charges);
}
