package com.mays.srm.billing.service;
import com.mays.srm.billing.dto.request.ServiceChargesRequestDTO;
import com.mays.srm.billing.dto.resDTO.ServiceChargesResponseDTO;
import com.mays.srm.core.service.GenericService;

public interface ServiceChargesService extends GenericService<ServiceChargesRequestDTO, ServiceChargesResponseDTO, Integer> {
}
