package com.mays.srm.inventory.service;
import com.mays.srm.core.service.GenericService;
import com.mays.srm.inventory.dto.request.PartsRequestDTO;
import com.mays.srm.inventory.dto.resDTO.PartsResponseDTO;

public interface PartsService extends GenericService<PartsRequestDTO, PartsResponseDTO, Integer> {
}
