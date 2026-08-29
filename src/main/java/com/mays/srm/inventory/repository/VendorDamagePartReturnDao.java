package com.mays.srm.inventory.repository;

import com.mays.srm.inventory.entities.VendorDamagePartReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorDamagePartReturnDao extends JpaRepository<VendorDamagePartReturn, Integer> {

    Optional<VendorDamagePartReturn> findByPartsMasterIndividualPartId(Integer individualPartId);
}
