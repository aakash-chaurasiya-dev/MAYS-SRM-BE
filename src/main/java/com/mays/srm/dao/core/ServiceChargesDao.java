package com.mays.srm.dao.core;

import com.mays.srm.entity.ServiceCharges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceChargesDao extends JpaRepository<ServiceCharges, Integer> {
}
