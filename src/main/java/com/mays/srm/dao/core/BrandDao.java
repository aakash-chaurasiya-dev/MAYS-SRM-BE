package com.mays.srm.dao.core;

import com.mays.srm.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandDao extends JpaRepository<Brand, Integer> {
    Optional<Brand> findByBrandName(String brandName);
}