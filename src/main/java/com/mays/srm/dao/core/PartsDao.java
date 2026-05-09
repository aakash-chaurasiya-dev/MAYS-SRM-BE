package com.mays.srm.dao.core;

import com.mays.srm.entity.Parts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartsDao extends JpaRepository<Parts, Integer> {
}
