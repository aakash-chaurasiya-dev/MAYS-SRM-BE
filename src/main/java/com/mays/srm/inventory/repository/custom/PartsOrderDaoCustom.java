package com.mays.srm.inventory.repository.custom;

import com.mays.srm.inventory.dto.resDTO.PartsOrderModalResponseDTO;
import com.mays.srm.inventory.dto.resDTO.PartsOrderSummaryResponseDTO;

import java.util.List;
import java.util.Optional;

public interface PartsOrderDaoCustom {

    Optional<PartsOrderModalResponseDTO> findModalByTicketPartId(Integer ticketPartId);

    Optional<PartsOrderModalResponseDTO> findModalByOrderId(Integer orderId);

    List<PartsOrderSummaryResponseDTO> findAllSummaries();
}
