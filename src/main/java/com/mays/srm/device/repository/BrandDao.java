package com.mays.srm.device.repository;
import com.mays.srm.device.entities.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandDao extends JpaRepository<Brand, Integer> {
    Optional<Brand> findByBrandName(String brandName);
}