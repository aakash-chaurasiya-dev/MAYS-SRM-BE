package com.mays.srm.inventory.repository;

import com.mays.srm.inventory.entities.PartSalesPrice;
import com.mays.srm.inventory.repository.custom.PartSalesPriceDaoCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartSalesPriceDao extends JpaRepository<PartSalesPrice, Integer>, PartSalesPriceDaoCustom {

    Optional<PartSalesPrice> findByProductListPartCatId(Integer partCatId);
}
