package com.mays.srm.inventory.service;

import com.mays.srm.inventory.dto.request.PartsOrderOpenRequestDTO;
import com.mays.srm.inventory.dto.request.PartsOrderSaveRequestDTO;
import com.mays.srm.inventory.dto.request.PartsOrderStatusRequestDTO;
import com.mays.srm.inventory.dto.resDTO.PartsOrderModalResponseDTO;
import com.mays.srm.inventory.dto.resDTO.PartsOrderSummaryResponseDTO;

import java.util.List;

public interface PartsOrderService {

    List<PartsOrderSummaryResponseDTO> getAll();

    PartsOrderModalResponseDTO getById(Integer orderId);

    PartsOrderModalResponseDTO getByTicketPartId(Integer ticketPartId);

    PartsOrderModalResponseDTO open(PartsOrderOpenRequestDTO request);

    PartsOrderModalResponseDTO create(PartsOrderSaveRequestDTO request);

    PartsOrderModalResponseDTO save(Integer orderId, PartsOrderSaveRequestDTO request);

    PartsOrderModalResponseDTO updateStatus(Integer orderId, PartsOrderStatusRequestDTO request);
}
