package com.mays.srm.billing.service;
import com.mays.srm.service.GenericService;
import com.mays.srm.billing.dto.request.ChargeTypeRequestDTO;
import com.mays.srm.billing.dto.resDTO.ChargeTypeResponseDTO;

public interface ChargeTypeService extends GenericService<ChargeTypeRequestDTO, ChargeTypeResponseDTO, Integer> {
}
