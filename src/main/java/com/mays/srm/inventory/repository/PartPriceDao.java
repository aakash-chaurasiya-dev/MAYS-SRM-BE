package com.mays.srm.inventory.repository;

import com.mays.srm.inventory.entities.PartPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartPriceDao extends JpaRepository<PartPrice, Integer> {

    Optional<PartPrice> findByIndividualPartId(Integer individualPartId);
}
