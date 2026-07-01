package com.mays.srm.billing.service;
import com.mays.srm.service.GenericService;
import com.mays.srm.billing.dto.request.ServiceChargesRequestDTO;
import com.mays.srm.billing.dto.resDTO.ServiceChargesResponseDTO;

public interface ServiceChargesService extends GenericService<ServiceChargesRequestDTO, ServiceChargesResponseDTO, Integer> {
}
