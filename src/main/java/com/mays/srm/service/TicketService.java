package com.mays.srm.service;

import com.mays.srm.dto.requestDTO.TicketRequestDTO;
import com.mays.srm.dto.responseDTO.TicketResponseDTO;

import java.util.List;

public interface TicketService extends GenericService<TicketRequestDTO, TicketResponseDTO, Integer> {
    List<TicketResponseDTO> getAllTicketsOfUser(Integer userId);
    List<TicketResponseDTO> getAllTicketsOfBranch(int branchId);
    List<TicketResponseDTO> getAllTicketsOfStatus(int statusId);
    List<TicketResponseDTO> getAllTicketsOfEmployee(int employeeId);
}
