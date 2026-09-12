package com.mays.srm.enquiry.repository;

import com.mays.srm.enquiry.entities.UserEntryReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEntryReportDao extends JpaRepository<UserEntryReport, Integer>, UserEntryReportDaoCustom {
}
