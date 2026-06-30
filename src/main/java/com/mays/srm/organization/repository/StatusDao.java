package com.mays.srm.organization.repository;
import com.mays.srm.organization.repository.StatusDaoCustom;
import com.mays.srm.organization.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusDao extends JpaRepository<Status, Integer>, StatusDaoCustom {
}


