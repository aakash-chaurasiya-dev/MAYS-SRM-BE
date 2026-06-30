package com.mays.srm.billing.repository;
import com.mays.srm.billing.repository.BillingDaoCustom;
import com.mays.srm.billing.entities.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingDao extends JpaRepository<Billing, Integer>, BillingDaoCustom {
}

