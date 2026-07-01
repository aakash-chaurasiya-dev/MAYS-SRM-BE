package com.mays.srm.billing.repository;
import com.mays.srm.billing.repository.ChargeTypeDaoCustom;
import com.mays.srm.billing.entities.ChargeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargeTypeDao extends JpaRepository<ChargeType, Integer>, ChargeTypeDaoCustom {
}

