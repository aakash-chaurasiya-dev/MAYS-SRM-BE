package com.mays.srm.ticket.repository;

import com.mays.srm.ticket.entities.WarrantyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarrantyTypeDao extends JpaRepository<WarrantyType, Integer> {
}
