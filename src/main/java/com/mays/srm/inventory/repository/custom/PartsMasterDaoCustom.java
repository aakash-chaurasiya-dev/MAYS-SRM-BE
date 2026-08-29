package com.mays.srm.inventory.repository.custom;

import com.mays.srm.inventory.dto.resDTO.PartsMasterLineResponseDTO;

import java.util.List;

public interface PartsMasterDaoCustom {

    List<PartsMasterLineResponseDTO> findLinesByOrderId(Integer orderId);
}
