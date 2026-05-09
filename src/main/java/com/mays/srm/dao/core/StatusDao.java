package com.mays.srm.dao.core;

import com.mays.srm.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusDao extends JpaRepository<Status, Integer> {
    List<Status> findByStatusTypeIgnoreCase(String statusType);
}
