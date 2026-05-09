package com.mays.srm.service;

import com.mays.srm.entity.Device;

import java.util.List;

public interface DeviceService extends GenericService<Device, String> {
    List<Device> findByModelName(String modelName);
    List<Device> findByBrandName(String brandName);
    List<Device> findByDeviceTypeName(String deviceType);
}