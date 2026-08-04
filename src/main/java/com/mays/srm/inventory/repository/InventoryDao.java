package com.mays.srm.inventory.repository;
import com.mays.srm.inventory.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryDao extends JpaRepository<Inventory, Integer> {
    Optional<Inventory> findFirstByProductNameIgnoreCase(String productName);
}
