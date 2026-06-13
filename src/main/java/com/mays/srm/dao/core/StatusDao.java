package com.mays.srm.dao.core;

import com.mays.srm.dao.custom.StatusDaoCustom;
import com.mays.srm.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusDao extends JpaRepository<Status, Integer>, StatusDaoCustom {
}
