package com.mays.srm.dao.core;

import com.mays.srm.entity.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingDao extends JpaRepository<Billing, Integer> {
}
