package com.mays.srm.inventory.repository;

import com.mays.srm.inventory.entities.Stocks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StocksDao extends JpaRepository<Stocks, Integer> {

    Optional<Stocks> findByProductListPartCatId(Integer partCatId);
}
