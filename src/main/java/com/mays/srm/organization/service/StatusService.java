package com.mays.srm.organization.service;
import com.mays.srm.service.GenericService;
import com.mays.srm.organization.dto.request.StatusRequestDTO;
import com.mays.srm.organization.dto.resDTO.StatusResponseDTO;

import java.util.List;

public interface StatusService extends GenericService<StatusRequestDTO, StatusResponseDTO, Integer> {
    List<StatusResponseDTO> getStatusesByType(String statusType);
}
