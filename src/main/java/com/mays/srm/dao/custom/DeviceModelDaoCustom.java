package com.mays.srm.dao.custom;

import com.mays.srm.entity.DeviceModel;

import java.util.List;
import java.util.Optional;

public interface DeviceModelDaoCustom {
    Optional<DeviceModel> findByModelNameAndBrandName(String modelName,String brandName);
    List<DeviceModel> findByModelName(String modelName);

}
