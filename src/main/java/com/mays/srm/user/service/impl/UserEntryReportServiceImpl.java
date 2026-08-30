package com.mays.srm.user.service.impl;

import com.mays.srm.user.dto.StatusCountDTO;
import com.mays.srm.user.dto.reqDTO.UserEntryReportRequestDTO;
import com.mays.srm.user.dto.resDTO.UserEntryReportResponseDTO;
import com.mays.srm.user.entities.UserEntryReport;
import com.mays.srm.user.entities.UserMaster;
import com.mays.srm.user.repository.UserEntryReportDao;
import com.mays.srm.user.repository.UserMasterDao;
import com.mays.srm.user.service.UserEntryReportService;
import com.mays.srm.enquiry.entities.Enquiry;
import com.mays.srm.enquiry.repository.EnquiryDao;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.ticket.repository.TicketDao;
import com.mays.srm.enquiry.entities.InwardRecord;
import com.mays.srm.enquiry.repository.InwardRecordDao;
import com.mays.srm.enquiry.entities.OutwardRecord;
import com.mays.srm.enquiry.repository.OutwardRecordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import com.mays.srm.util.RestPageImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserEntryReportServiceImpl implements UserEntryReportService {

    @Autowired
    private UserEntryReportDao reportDao;

    @Autowired
    private UserMasterDao userMasterDao;

    @Autowired
    private EnquiryDao enquiryDao;

    @Autowired
    private TicketDao ticketDao;

    @Autowired
    private InwardRecordDao inwardRecordDao;

    @Autowired
    private OutwardRecordDao outwardRecordDao;

    @Override
    @Transactional
    @CacheEvict(value = "todayEntries", allEntries = true)
    public UserEntryReportResponseDTO saveEntry(UserEntryReportRequestDTO dto) {
        Optional<UserMaster> userOpt = userMasterDao.findById(dto.getUserId());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        UserEntryReport report = new UserEntryReport();
        report.setUser(userOpt.get());
        report.setReason(dto.getReason());
        
        // Handle entry type
        if (dto.getEntryType() != null) {
            report.setEntryType(dto.getEntryType());
        } else {
            // Deduce from reason for backward compatibility
            String reasonLower = dto.getReason() != null ? dto.getReason().trim().toLowerCase() : "";
            if (reasonLower.equals("inward")) report.setEntryType("INWARD");
            else if (reasonLower.equals("outward")) report.setEntryType("OUTWARD");
            else if (reasonLower.equals("enquiry")) report.setEntryType("ENQUIRY");
            else report.setEntryType("OTHER");
        }

        // Link references
        if (dto.getEnquiryId() != null) {
            enquiryDao.findById(dto.getEnquiryId()).ifPresent(report::setEnquiry);
        }
        if (dto.getTicketId() != null) {
            ticketDao.findById(dto.getTicketId()).ifPresent(report::setTicket);
        }
        if (dto.getInwardId() != null) {
            inwardRecordDao.findById(dto.getInwardId()).ifPresent(report::setInward);
        }
        if (dto.getOutwardId() != null) {
            outwardRecordDao.findById(dto.getOutwardId()).ifPresent(report::setOutward);
        }

        report = reportDao.save(report);
        return mapToDTO(report);
    }

    @Override
    @Cacheable(value = "todayEntries", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<UserEntryReportResponseDTO> getTodayReports(Pageable pageable) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        Page<UserEntryReport> page = reportDao.findByDateRange(startOfDay, endOfDay, pageable);
        List<UserEntryReportResponseDTO> dtos = page.getContent().stream().map(this::mapToDTO).toList();
        return new RestPageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Override
    public Page<UserEntryReportResponseDTO> getReportsByDateRange(LocalDate start, LocalDate end, Pageable pageable) {
        LocalDateTime startOfDay = start.atStartOfDay();
        LocalDateTime endOfDay = end.plusDays(1).atStartOfDay();
        Page<UserEntryReport> page = reportDao.findByDateRange(startOfDay, endOfDay, pageable);
        List<UserEntryReportResponseDTO> dtos = page.getContent().stream().map(this::mapToDTO).toList();
        return new RestPageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Override
    public StatusCountDTO getStatusCounts(LocalDate start, LocalDate end) {
        LocalDateTime startOfDay;
        LocalDateTime endOfDay;
        
        if (start != null && end != null) {
            startOfDay = start.atStartOfDay();
            endOfDay = end.plusDays(1).atStartOfDay();
        } else {
            startOfDay = LocalDate.now().atStartOfDay();
            endOfDay = startOfDay.plusDays(1);
        }
        
        List<UserEntryReport> reports = reportDao.findAllByDateRange(startOfDay, endOfDay);

        long inward = 0, outward = 0, enquiry = 0, ticketStatus = 0, others = 0;

        for (UserEntryReport r : reports) {
            String reason = r.getReason() != null ? r.getReason().trim().toLowerCase() : "";
            String entryType = r.getEntryType() != null ? r.getEntryType().trim().toLowerCase() : "";

            if (reason.equals("inward") || entryType.equals("inward")) inward++;
            else if (reason.equals("outward") || entryType.equals("outward")) outward++;
            else if (reason.equals("enquiry") || entryType.equals("enquiry")) enquiry++;
            else if (reason.equals("ticket status check") || entryType.equals("ticket status check")) ticketStatus++;
            else others++;
        }

        StatusCountDTO counts = new StatusCountDTO(reports.size(), inward, outward, enquiry, ticketStatus, others);
        return counts;
    }

    private UserEntryReportResponseDTO mapToDTO(UserEntryReport report) {
        UserEntryReportResponseDTO dto = new UserEntryReportResponseDTO();
        dto.setEntryNo(report.getEntryNo());
        dto.setUserId(report.getUser().getUserId());
        dto.setUserName(report.getUser().getFirstName() + " " + report.getUser().getLastName());
        dto.setReason(report.getReason());
        dto.setEntryType(report.getEntryType());
        dto.setEnquiryId(report.getEnquiry() != null ? report.getEnquiry().getEnquiryId() : null);
        dto.setTicketId(report.getTicket() != null ? report.getTicket().getTicketId() : null);
        dto.setInwardId(report.getInward() != null ? report.getInward().getInwardId() : null);
        dto.setOutwardId(report.getOutward() != null ? report.getOutward().getOutwardId() : null);
        dto.setEntryDate(report.getEntryDate());
        return dto;
    }
}
