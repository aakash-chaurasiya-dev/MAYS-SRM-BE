package com.mays.srm.inventory.service;
import com.mays.srm.core.service.GenericService;
import com.mays.srm.inventory.dto.request.InventoryRequestDTO;
import com.mays.srm.inventory.dto.resDTO.InventoryResponseDTO;

public interface InventoryService extends GenericService<InventoryRequestDTO, InventoryResponseDTO, Integer> {
}
