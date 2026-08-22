package com.mays.srm.inventory.repository;

import com.mays.srm.inventory.entities.ProductList;
import com.mays.srm.inventory.repository.custom.ProductListDaoCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductListDao extends JpaRepository<ProductList, Integer>, ProductListDaoCustom {
}
