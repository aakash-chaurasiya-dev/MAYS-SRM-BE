package com.mays.srm.inventory.repository;
import com.mays.srm.inventory.entities.Parts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartsDao extends JpaRepository<Parts, Integer> {
}

