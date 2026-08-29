package com.mays.srm.inventory.repository.custom;

import com.mays.srm.inventory.dto.resDTO.ProductListOptionDTO;
import com.mays.srm.inventory.dto.resDTO.ProductListResponseDTO;

import java.util.List;
import java.util.Optional;

public interface ProductListDaoCustom {

    List<ProductListOptionDTO> searchOptions(String term, int limit);

    List<ProductListResponseDTO> findAllDetails();

    Optional<ProductListResponseDTO> findDetailsById(Integer partCatId);
}
