package com.mays.srm.dao.core;
import com.mays.srm.dao.custom.ChargeTypeDaoCustom;
import com.mays.srm.billing.entities.ChargeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargeTypeDao extends JpaRepository<ChargeType, Integer>, ChargeTypeDaoCustom {
}
