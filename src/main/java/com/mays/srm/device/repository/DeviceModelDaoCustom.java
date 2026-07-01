package com.mays.srm.device.repository;
import com.mays.srm.device.entities.DeviceModel;

import java.util.List;
import java.util.Optional;

public interface DeviceModelDaoCustom {
    Optional<DeviceModel> findByModelNameAndBrandName(String modelName,String brandName);
    List<DeviceModel> findByModelName(String modelName);

}

