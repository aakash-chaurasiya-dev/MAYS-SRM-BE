package com.mays.srm.device.repository;

import com.mays.srm.device.entities.DeviceAccessoryMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceAccessoryMasterDao extends JpaRepository<DeviceAccessoryMaster, Integer> {
    List<DeviceAccessoryMaster> findByDeviceType_DeviceTypeId(Integer deviceTypeId);
}
