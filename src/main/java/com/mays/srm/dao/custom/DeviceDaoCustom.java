package com.mays.srm.dao.custom;
import com.mays.srm.device.entities.Device;

import java.util.List;

public interface DeviceDaoCustom {
    List<Device> findByModelName(String modelName);
    List<Device> findByBrandName(String brandName);
    List<Device> findByDeviceTypeName(String deviceType);
}
