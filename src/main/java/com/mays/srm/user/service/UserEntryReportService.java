package com.mays.srm.user.service;

import com.mays.srm.user.dto.UserEntryReportRequestDTO;
import com.mays.srm.user.dto.UserEntryReportResponseDTO;
import com.mays.srm.user.dto.StatusCountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

public interface UserEntryReportService {
    UserEntryReportResponseDTO saveEntry(UserEntryReportRequestDTO dto);
    Page<UserEntryReportResponseDTO> getTodayReports(Pageable pageable);
    Page<UserEntryReportResponseDTO> getReportsByDateRange(LocalDate start, LocalDate end, Pageable pageable);
    StatusCountDTO getStatusCounts(LocalDate start, LocalDate end);
}
