package com.mays.srm.enquiry.service;

import com.mays.srm.enquiry.dto.reqDTO.UserEntryReportRequestDTO;
import com.mays.srm.enquiry.dto.resDTO.UserEntryReportResponseDTO;
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
