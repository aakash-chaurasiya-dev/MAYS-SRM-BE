package com.mays.srm.dao.core;

import com.mays.srm.dao.custom.DeviceDaoCustom;
import com.mays.srm.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceDao extends JpaRepository<Device, String>, DeviceDaoCustom {
}
