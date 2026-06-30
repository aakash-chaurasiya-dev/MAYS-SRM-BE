package com.mays.srm.device.service;

import com.mays.srm.core.service.GenericService;
import com.mays.srm.device.dto.request.DeviceAccessoryMasterRequestDTO;
import com.mays.srm.device.dto.resDTO.DeviceAccessoryMasterResponseDTO;

import java.util.List;

public interface DeviceAccessoryMasterService extends GenericService<DeviceAccessoryMasterRequestDTO, DeviceAccessoryMasterResponseDTO, Integer> {
    List<DeviceAccessoryMasterResponseDTO> getByDeviceTypeId(Integer deviceTypeId);
}
