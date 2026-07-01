package com.mays.srm.billing.repository;
import com.mays.srm.billing.entities.ServiceCharges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceChargesDao extends JpaRepository<ServiceCharges, Integer> {
}
