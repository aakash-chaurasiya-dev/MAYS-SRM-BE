package com.mays.srm.inventory.repository;

import com.mays.srm.inventory.entities.InStockPart;
import com.mays.srm.inventory.repository.custom.InStockPartDaoCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InStockPartDao extends JpaRepository<InStockPart, Integer>, InStockPartDaoCustom {
}
