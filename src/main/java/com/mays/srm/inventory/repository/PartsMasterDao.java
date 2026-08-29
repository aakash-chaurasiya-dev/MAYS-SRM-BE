package com.mays.srm.inventory.repository;

import com.mays.srm.inventory.entities.PartsMaster;
import com.mays.srm.inventory.repository.custom.PartsMasterDaoCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartsMasterDao extends JpaRepository<PartsMaster, Integer>, PartsMasterDaoCustom {

    List<PartsMaster> findByPartsOrder_OrderIdAndIsActiveTrueOrderByIndividualPartIdAsc(Integer orderId);

    long countByPartsOrder_OrderIdAndIsActiveTrue(Integer orderId);

    long countByPartsOrder_OrderIdAndIsActiveTrueAndReceivedTrue(Integer orderId);
}
