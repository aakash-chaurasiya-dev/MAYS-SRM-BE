package com.mays.srm.user.repository;

import com.mays.srm.user.entities.UserEntryReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEntryReportDao extends JpaRepository<UserEntryReport, Integer>, UserEntryReportDaoCustom {
}
