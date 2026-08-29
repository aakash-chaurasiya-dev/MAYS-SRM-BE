package com.mays.srm.inventory.service;

import com.mays.srm.inventory.dto.request.QuoteRequestDTO;
import com.mays.srm.inventory.dto.resDTO.QuoteResponseDTO;

import java.util.List;

public interface QuoteService {

    List<QuoteResponseDTO> getByTicketPartId(Integer ticketPartId);

    List<QuoteResponseDTO> getByTicketId(Integer ticketId);

    QuoteResponseDTO create(QuoteRequestDTO request);

    QuoteResponseDTO update(Integer quoteId, QuoteRequestDTO request);

    QuoteResponseDTO send(Integer quoteId);
}
