package com.mays.srm.inventory.service;

import com.mays.srm.inventory.dto.request.ProductListRequestDTO;
import com.mays.srm.inventory.dto.resDTO.ProductListOptionDTO;
import com.mays.srm.inventory.dto.resDTO.ProductListResponseDTO;

import java.util.List;

public interface ProductListService {

    List<ProductListOptionDTO> search(String term, int limit);

    List<ProductListResponseDTO> getAll();

    ProductListResponseDTO getById(Integer partCatId);

    ProductListResponseDTO create(ProductListRequestDTO request);

    ProductListResponseDTO update(Integer partCatId, ProductListRequestDTO request);

    void delete(Integer partCatId);

    void deleteBulk(List<Integer> ids);
}
