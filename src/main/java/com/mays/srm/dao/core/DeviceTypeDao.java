package com.mays.srm.dao.core;

import com.mays.srm.entity.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceTypeDao extends JpaRepository<DeviceType, Integer> {
}
