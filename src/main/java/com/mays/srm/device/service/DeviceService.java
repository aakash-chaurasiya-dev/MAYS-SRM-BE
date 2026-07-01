package com.mays.srm.device.service;
import com.mays.srm.service.GenericService;
import com.mays.srm.device.dto.request.DeviceRequestDTO;
import com.mays.srm.device.dto.resDTO.DeviceResponseDTO;

public interface DeviceService extends GenericService<DeviceRequestDTO, DeviceResponseDTO, String> {
}
