package com.mays.srm.inventory.repository.custom;

import com.mays.srm.inventory.dto.resDTO.InStockPartOptionDTO;
import com.mays.srm.inventory.dto.resDTO.InStockPartResponseDTO;

import java.util.List;
import java.util.Optional;

public interface InStockPartDaoCustom {

    List<InStockPartOptionDTO> findAvailableOptions(Integer partCatId);

    List<InStockPartResponseDTO> findAllDetails(Integer partCatId);

    Optional<InStockPartResponseDTO> findDetailsById(Integer individualPartId);
}
