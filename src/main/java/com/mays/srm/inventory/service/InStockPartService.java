package com.mays.srm.inventory.service;

import com.mays.srm.inventory.dto.request.InStockPartRequestDTO;
import com.mays.srm.inventory.dto.resDTO.InStockPartOptionDTO;
import com.mays.srm.inventory.dto.resDTO.InStockPartResponseDTO;

import java.util.List;

public interface InStockPartService {

    List<InStockPartOptionDTO> findAvailable(Integer partCatId);

    List<InStockPartResponseDTO> getAll(Integer partCatId);

    InStockPartResponseDTO getById(Integer individualPartId);

    InStockPartResponseDTO create(InStockPartRequestDTO request);

    InStockPartResponseDTO update(Integer individualPartId, InStockPartRequestDTO request);

    void delete(Integer individualPartId);

    void deleteBulk(List<Integer> ids);
}
