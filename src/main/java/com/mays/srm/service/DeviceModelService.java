package com.mays.srm.service;

import com.mays.srm.entity.DeviceModel;

import java.util.List;
import java.util.Optional;

public interface DeviceModelService extends GenericService<DeviceModel, Integer> {
    Optional<DeviceModel> findByModelNameAndBrandName(String modelName, String brandName);
    List<DeviceModel> findByModelName(String modelName);
}