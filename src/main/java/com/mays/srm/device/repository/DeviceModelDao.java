package com.mays.srm.device.repository;
import com.mays.srm.device.repository.DeviceModelDaoCustom;
import com.mays.srm.device.entities.DeviceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceModelDao extends JpaRepository<DeviceModel, Integer>, DeviceModelDaoCustom {
    Optional<DeviceModel> findByModelNameIgnoreCaseAndBrandBrandId(String modelName, Integer brandId);
}

