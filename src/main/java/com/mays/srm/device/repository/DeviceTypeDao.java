package com.mays.srm.device.repository;
import com.mays.srm.device.entities.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceTypeDao extends JpaRepository<DeviceType, Integer> {
}
