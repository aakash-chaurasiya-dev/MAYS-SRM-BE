package com.mays.srm.inventory.repository.custom;

import com.mays.srm.inventory.dto.resDTO.PartPriceCombinedResponseDTO;

import java.util.List;
import java.util.Optional;

public interface PartSalesPriceDaoCustom {

    List<PartPriceCombinedResponseDTO> findAllCombined();

    Optional<PartPriceCombinedResponseDTO> findCombinedByPartCatId(Integer partCatId);
}
