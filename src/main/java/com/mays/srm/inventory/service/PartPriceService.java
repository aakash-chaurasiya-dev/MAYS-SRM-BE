package com.mays.srm.inventory.service;

import com.mays.srm.inventory.dto.request.PartPriceCombinedRequestDTO;
import com.mays.srm.inventory.dto.resDTO.PartPriceCombinedResponseDTO;

import java.util.List;

public interface PartPriceService {

    List<PartPriceCombinedResponseDTO> getAll();

    PartPriceCombinedResponseDTO getByPartCatId(Integer partCatId);

    PartPriceCombinedResponseDTO upsert(PartPriceCombinedRequestDTO request);

    PartPriceCombinedResponseDTO update(Integer partCatId, PartPriceCombinedRequestDTO request);

    void delete(Integer partCatId);
}
