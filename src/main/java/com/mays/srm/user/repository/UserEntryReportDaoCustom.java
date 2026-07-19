package com.mays.srm.user.repository;

import com.mays.srm.user.entities.UserEntryReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface UserEntryReportDaoCustom {
    Page<UserEntryReport> findByDateRange(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);
    List<UserEntryReport> findAllByDateRange(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
