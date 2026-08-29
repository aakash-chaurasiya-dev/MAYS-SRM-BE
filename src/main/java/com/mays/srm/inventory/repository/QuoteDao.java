package com.mays.srm.inventory.repository;

import com.mays.srm.inventory.entities.Quote;
import com.mays.srm.inventory.repository.custom.QuoteDaoCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteDao extends JpaRepository<Quote, Integer>, QuoteDaoCustom {
}
