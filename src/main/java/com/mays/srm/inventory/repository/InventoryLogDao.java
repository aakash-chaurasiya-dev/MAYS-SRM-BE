package com.mays.srm.inventory.repository;

import com.mays.srm.inventory.entities.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryLogDao extends JpaRepository<InventoryLog, Integer> {
}
