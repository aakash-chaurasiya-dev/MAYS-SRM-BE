package com.mays.srm.dao.core;

import com.mays.srm.dao.custom.DeviceModelDaoCustom;
import com.mays.srm.entity.DeviceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceModelDao extends JpaRepository<DeviceModel, Integer>, DeviceModelDaoCustom {
}
