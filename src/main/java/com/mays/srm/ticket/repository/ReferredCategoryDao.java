package com.mays.srm.ticket.repository;

import com.mays.srm.ticket.entities.ReferredCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferredCategoryDao extends JpaRepository<ReferredCategory, Integer> {
}
