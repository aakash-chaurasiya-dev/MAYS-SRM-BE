package com.mays.srm.service;

import com.mays.srm.dto.requestDTO.StatusRequestDTO;
import com.mays.srm.dto.responseDTO.StatusResponseDTO;

import java.util.List;

public interface StatusService extends GenericService<StatusRequestDTO, StatusResponseDTO, Integer> {
    List<StatusResponseDTO> getStatusesByType(String statusType);
}
