package com.mays.srm.inventory.repository.custom;

import com.mays.srm.inventory.dto.resDTO.QuoteResponseDTO;

import java.util.List;

public interface QuoteDaoCustom {

    List<QuoteResponseDTO> findDetailsByTicketPartId(Integer ticketPartId);

    List<QuoteResponseDTO> findDetailsByTicketId(Integer ticketId);
}
