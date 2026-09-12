package com.mays.srm.enquiry.service.impl;

import com.mays.srm.enquiry.dto.reqDTO.UserEntryReportRequestDTO;
import com.mays.srm.enquiry.dto.resDTO.UserEntryReportResponseDTO;
import com.mays.srm.enquiry.entities.Enquiry;
import com.mays.srm.enquiry.entities.UserEntryReport;
import com.mays.srm.enquiry.repository.EnquiryDao;
import com.mays.srm.enquiry.repository.UserEntryReportDao;
import com.mays.srm.enquiry.service.UserEntryReportService;
import com.mays.srm.user.dto.StatusCountDTO;
import com.mays.srm.user.entities.UserMaster;
import com.mays.srm.user.repository.UserMasterDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class UserEntryReportServiceImpl implements UserEntryReportService {

    @Autowired
    private UserEntryReportDao reportDao;

    @Autowired
    private UserMasterDao userMasterDao;

    @Autowired
    private EnquiryDao enquiryDao;

    @Override
    @Transactional
    @CacheEvict(value = "todayEntries", allEntries = true)
    public UserEntryReportResponseDTO saveEntry(UserEntryReportRequestDTO dto) {
        log.info("Saving user entry report for userId: {}", dto.getUserId());

        UserMaster user = userMasterDao.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));

        UserEntryReport report = new UserEntryReport();
        report.setUser(user);
        report.setReason(dto.getReason());

        if (dto.getEnquiryId() != null) {
            Enquiry enquiry = enquiryDao.findById(dto.getEnquiryId())
                    .orElseThrow(() -> new RuntimeException("Enquiry not found with id: " + dto.getEnquiryId()));
            report.setEnquiry(enquiry);
        }

        report = reportDao.save(report);
        log.info("Saved entry report with entryNo: {}", report.getEntryNo());

        return mapToDTO(report);
    }

    @Override
    @Cacheable(value = "todayEntries", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<UserEntryReportResponseDTO> getTodayReports(Pageable pageable) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        Page<UserEntryReport> page = reportDao.findByDateRange(startOfDay, endOfDay, pageable);
        return page.map(this::mapToDTO);
    }

    @Override
    public Page<UserEntryReportResponseDTO> getReportsByDateRange(LocalDate start, LocalDate end, Pageable pageable) {
        LocalDateTime startOfDay = start.atStartOfDay();
        LocalDateTime endOfDay = end.plusDays(1).atStartOfDay();
        Page<UserEntryReport> page = reportDao.findByDateRange(startOfDay, endOfDay, pageable);
        return page.map(this::mapToDTO);
    }

    @Override
    public StatusCountDTO getStatusCounts(LocalDate start, LocalDate end) {
        LocalDateTime startOfDay = (start != null) ? start.atStartOfDay() : LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = (end != null) ? end.plusDays(1).atStartOfDay() : startOfDay.plusDays(1);

        List<UserEntryReport> reports = reportDao.findAllByDateRange(startOfDay, endOfDay);

        long inward = 0, outward = 0, enquiry = 0, ticketStatus = 0, others = 0;

        for (UserEntryReport r : reports) {
            String reason = r.getReason() != null ? r.getReason().trim().toLowerCase() : "";

            if (reason.equals("inward")) {
                inward++;
            } else if (reason.equals("outward")) {
                outward++;
            } else if (reason.equals("enquiry")) {
                enquiry++;
            } else if (reason.equals("ticket status check")) {
                ticketStatus++;
            } else {
                others++;
            }
        }

        return new StatusCountDTO(reports.size(), inward, outward, enquiry, ticketStatus, others);
    }

    // ----- Mapper -----
    private UserEntryReportResponseDTO mapToDTO(UserEntryReport report) {
        UserEntryReportResponseDTO dto = new UserEntryReportResponseDTO();
        dto.setEntryNo(report.getEntryNo());
        dto.setUserId(report.getUser().getUserId());
        dto.setUserName(report.getUser().getFirstName() + " " + report.getUser().getLastName());
        dto.setReason(report.getReason());
        dto.setEntryDate(report.getEntryDate());  // LocalDateTime → Date? DTO me Date hai, but hum LocalDateTime set kar sakte hain agar DTO bhi LocalDateTime ho jaye.
        // Note: Agar DTO me Date hai, toh yahan type mismatch ho sakta hai. Better hai DTO ko LocalDateTime me change karein.
        // Main maan raha hoon ki aap DTO me bhi LocalDateTime kar lenge.
        dto.setEnquiryId(report.getEnquiry() != null ? report.getEnquiry().getEnquiryId() : null);

        // Extra fields from Enquiry
        if (report.getEnquiry() != null) {
            Enquiry enquiry = report.getEnquiry();
            dto.setSerialNo(enquiry.getSerialNo());
            if (enquiry.getDeviceModel() != null) {
                dto.setModelName(enquiry.getDeviceModel().getModelName());
            }
            if (enquiry.getDeviceModel() != null && enquiry.getDeviceModel().getBrand() != null) {
                dto.setBrandName(enquiry.getDeviceModel().getBrand().getBrandName());
            }
            if (enquiry.getDeviceModel().getBrand().getDeviceType() != null) {
                dto.setDeviceTypeName(enquiry.getDeviceModel().getBrand().getDeviceType().getDeviceTypeName());
            }
        }

        return dto;
    }
}