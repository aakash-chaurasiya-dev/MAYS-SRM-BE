package com.mays.srm.device.repository;
import com.mays.srm.device.repository.DeviceDaoCustom;
import com.mays.srm.device.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceDao extends JpaRepository<Device, String>, DeviceDaoCustom {
}

