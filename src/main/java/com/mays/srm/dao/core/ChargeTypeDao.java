package com.mays.srm.dao.core;

import com.mays.srm.entity.ChargeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargeTypeDao extends JpaRepository<ChargeType, Integer> {
}
