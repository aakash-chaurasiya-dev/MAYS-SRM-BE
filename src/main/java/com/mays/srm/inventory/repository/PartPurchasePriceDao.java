package com.mays.srm.inventory.repository;

import com.mays.srm.inventory.entities.PartPurchasePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartPurchasePriceDao extends JpaRepository<PartPurchasePrice, Integer> {

    Optional<PartPurchasePrice> findByProductListPartCatId(Integer partCatId);
}
