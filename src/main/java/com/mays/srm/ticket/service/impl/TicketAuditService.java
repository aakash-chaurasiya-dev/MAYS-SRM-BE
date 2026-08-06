package com.mays.srm.ticket.service.impl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mays.srm.organization.repository.StatusDao;
import com.mays.srm.ticket.repository.TicketLogsDao;
import com.mays.srm.ticket.dto.request.TicketRequestDTO;
import com.mays.srm.user.entities.Employee;
import com.mays.srm.user.repository.EmployeeDao;
import com.mays.srm.organization.entities.Status;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.ticket.entities.TicketLogs;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.security.util.SecurityUtils;
import com.mays.srm.timetracking.util.StatusAccessValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class TicketAuditService {

    private final TicketLogsDao ticketLogsDao;
    private final StatusDao statusDao;
    private final EmployeeDao employeeDao;
    private final ObjectMapper objectMapper;

    @Autowired
    public TicketAuditService(TicketLogsDao ticketLogsDao, StatusDao statusDao, EmployeeDao employeeDao, ObjectMapper objectMapper) {
        this.ticketLogsDao = ticketLogsDao;
        this.statusDao = statusDao;
        this.employeeDao = employeeDao;
        this.objectMapper = objectMapper;
    }

    private void checkChange(String fieldName, Object oldValue, Object newValue, Map<String, Map<String, Object>> changes) {
        if (newValue != null && !Objects.equals(oldValue, newValue)) {
            // Treat empty strings as no change if old was null or empty
            if (newValue instanceof String && ((String) newValue).trim().isEmpty()) {
                if (oldValue == null || ((String) oldValue).trim().isEmpty()) {
                    return;
                }
            }
            Map<String, Object> diff = new HashMap<>();
            diff.put("old", oldValue);
            diff.put("new", newValue);
            changes.put(fieldName, diff);
        }
    }

    /**
     * Checks if any fields changed and logs it appropriately.
     */
    public void logChanges(Ticket ticket, TicketRequestDTO requestDTO) {
        Map<String, Map<String, Object>> changes = new HashMap<>();

        // 1. Status Check
        boolean statusChanged = false;
        if (requestDTO.getTicketStatusId() != null) {
            Integer oldStatusId = ticket.getTicketStatus() != null ? ticket.getTicketStatus().getStatusId() : null;
            if (!Objects.equals(oldStatusId, requestDTO.getTicketStatusId())) {
                statusChanged = true;
                checkChange("ticketStatusId", oldStatusId, requestDTO.getTicketStatusId(), changes);
            }
        }

        // 2. Employee (Assignee) Check
        boolean employeeChanged = false;
        if (requestDTO.getEmployeeId() != null) {
            Integer oldAssigneeId = ticket.getEmployee() != null ? ticket.getEmployee().getEmployeeId() : null;
            if (!Objects.equals(oldAssigneeId, requestDTO.getEmployeeId())) {
                employeeChanged = true;
                checkChange("assigneeEmployeeId", oldAssigneeId, requestDTO.getEmployeeId(), changes);
            }
        }

        // 3. Other straightforward fields
        checkChange("priority", ticket.getPriority(), requestDTO.getPriority(), changes);
        checkChange("ticketDescription", ticket.getTicketDescription(), requestDTO.getTicketDescription(), changes);
        checkChange("emailId", ticket.getEmailId(), requestDTO.getEmailId(), changes);
        if (requestDTO.getWarrantyTypeId() != null) {
            Integer oldWarrantyTypeId = ticket.getWarrantyType() != null ? ticket.getWarrantyType().getWarrantyTypeId() : null;
            checkChange("warrantyTypeId", oldWarrantyTypeId, requestDTO.getWarrantyTypeId(), changes);
        }
        if (requestDTO.getReferredCategoryId() != null) {
            Integer oldReferredCategoryId = ticket.getReferredCategory() != null ? ticket.getReferredCategory().getReferredCategoryId() : null;
            checkChange("referredCategoryId", oldReferredCategoryId, requestDTO.getReferredCategoryId(), changes);
        }
        checkChange("referredCategoryDecriptionTicket", ticket.getReferredCategoryDecriptionTicket(), requestDTO.getReferredCategoryDecriptionTicket(), changes);
        checkChange("targetDate", ticket.getTargetDate(), requestDTO.getTargetDate(), changes);
        checkChange("closedDate", ticket.getClosedDate(), requestDTO.getClosedDate(), changes);
        
        if (requestDTO.getTicketBranchId() != null) {
            Integer oldBranchId = ticket.getTicketBranch() != null ? ticket.getTicketBranch().getBranchId() : null;
            checkChange("ticketBranchId", oldBranchId, requestDTO.getTicketBranchId(), changes);
        }
        if (requestDTO.getTicketTypeId() != null) {
            Integer oldTypeId = ticket.getTicketType() != null ? ticket.getTicketType().getTicketTypeId() : null;
            checkChange("ticketTypeId", oldTypeId, requestDTO.getTicketTypeId(), changes);
        }
        if (requestDTO.getUserRefNo() != null) {
            String oldUserRef = ticket.getUserMaster() != null ? ticket.getUserMaster().getMobileNo() : null;
            checkChange("userRefNo", oldUserRef, requestDTO.getUserRefNo(), changes);
        }
        if (requestDTO.getDeviceSerialNo() != null) {
            String oldDeviceSerial = ticket.getDevice() != null ? ticket.getDevice().getSerialNo() : null;
            checkChange("deviceSerialNo", oldDeviceSerial, requestDTO.getDeviceSerialNo(), changes);
        }

        if (requestDTO.getVendorId() != null) {
            Integer oldVendorId = ticket.getVendor() != null ? ticket.getVendor().getId() : null;
            checkChange("vendorId", oldVendorId, requestDTO.getVendorId(), changes);
        }
        if (requestDTO.getVendorUserId() != null) {
            Integer oldVendorUserId = ticket.getVendorUser() != null ? ticket.getVendorUser().getId() : null;
            checkChange("vendorUserId", oldVendorUserId, requestDTO.getVendorUserId(), changes);
        }
        if (requestDTO.getParentTicketId() != null) {
            Integer oldParentTicketId = ticket.getParentTicket() != null ? ticket.getParentTicket().getTicketId() : null;
            checkChange("parentTicketId", oldParentTicketId, requestDTO.getParentTicketId(), changes);
        }
        if (requestDTO.getDeviceModelId() != null) {
            Integer oldDeviceModelId = (ticket.getDevice() != null && ticket.getDevice().getModel() != null) ? ticket.getDevice().getModel().getModelId() : null;
            checkChange("deviceModelId", oldDeviceModelId, requestDTO.getDeviceModelId(), changes);
        }
        if (requestDTO.getBrandId() != null) {
            Integer oldBrandId = (ticket.getDevice() != null && ticket.getDevice().getModel() != null && ticket.getDevice().getModel().getBrand() != null) ? ticket.getDevice().getModel().getBrand().getBrandId() : null;
            checkChange("brandId", oldBrandId, requestDTO.getBrandId(), changes);
        }
        if (requestDTO.getCustomModelName() != null) {
            String oldCustomModel = (ticket.getDevice() != null && ticket.getDevice().getModel() != null) ? ticket.getDevice().getModel().getModelName() : null;
            checkChange("customModelName", oldCustomModel, requestDTO.getCustomModelName(), changes);
        }
        boolean hasRemarks = requestDTO.getRemarks() != null && !requestDTO.getRemarks().trim().isEmpty();

        if (!changes.isEmpty() || hasRemarks) {
            TicketLogs log = new TicketLogs();
            log.setTicket(ticket);
            log.setModificationDate(LocalDateTime.now());
            log.setAssignorRemarks(requestDTO.getRemarks());
            log.setOldStatus(ticket.getTicketStatus());
            log.setAssignorEmployee(ticket.getEmployee());

            if (requestDTO.getModifiedByEmployeeId() != null) {
                Employee modifier = employeeDao.findById(requestDTO.getModifiedByEmployeeId()).orElse(null);
                log.setModifiedBy(modifier);
            }

            try {
                String changedFieldsJson = changes.isEmpty() ? null : objectMapper.writeValueAsString(changes);
                log.setChangedFields(changedFieldsJson);
            } catch (Exception e) {
                // Should not happen with Map serialization
            }

            if (statusChanged) {
                Status newStatus = statusDao.findById(requestDTO.getTicketStatusId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid status ID"));
                ticket.setTicketStatus(newStatus);
                log.setNewStatus(newStatus);
            } else {
                log.setNewStatus(log.getOldStatus());
            }

            if (employeeChanged) {
                Employee newAssignee = employeeDao.findById(requestDTO.getEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "New assignee employee not found with ID: " + requestDTO.getEmployeeId()));
                ticket.setEmployee(newAssignee);
                log.setAssigneeEmployee(newAssignee);
            } else {
                log.setAssigneeEmployee(ticket.getEmployee());
            }

            ticket.setModNo((ticket.getModNo() == null ? 0 : ticket.getModNo()) + 1);
            ticketLogsDao.save(log);
        }
    }

    /**
     * Logs the initial creation of a ticket (null → initial values). Does not mutate ticket relations.
     */
    public void logTicketCreated(Ticket savedTicket, TicketRequestDTO requestDTO) {
        Map<String, Map<String, Object>> changes = new HashMap<>();

        if (savedTicket.getTicketStatus() != null) {
            checkChange("ticketStatusId", null, savedTicket.getTicketStatus().getStatusId(), changes);
        } else if (requestDTO.getTicketStatusId() != null) {
            checkChange("ticketStatusId", null, requestDTO.getTicketStatusId(), changes);
        }
        if (savedTicket.getEmployee() != null) {
            checkChange("assigneeEmployeeId", null, savedTicket.getEmployee().getEmployeeId(), changes);
        } else if (requestDTO.getEmployeeId() != null) {
            checkChange("assigneeEmployeeId", null, requestDTO.getEmployeeId(), changes);
        }

        checkChange("priority", null, requestDTO.getPriority(), changes);
        checkChange("ticketDescription", null, requestDTO.getTicketDescription(), changes);
        checkChange("emailId", null, requestDTO.getEmailId(), changes);
        checkChange("warrantyTypeId", null, requestDTO.getWarrantyTypeId(), changes);
        checkChange("referredCategoryId", null, requestDTO.getReferredCategoryId(), changes);
        checkChange("referredCategoryDecriptionTicket", null, requestDTO.getReferredCategoryDecriptionTicket(), changes);
        checkChange("targetDate", null, requestDTO.getTargetDate(), changes);
        checkChange("closedDate", null, requestDTO.getClosedDate(), changes);
        checkChange("ticketBranchId", null, requestDTO.getTicketBranchId(), changes);
        checkChange("ticketTypeId", null, requestDTO.getTicketTypeId(), changes);
        checkChange("userRefNo", null, requestDTO.getUserRefNo(), changes);
        checkChange("deviceSerialNo", null, requestDTO.getDeviceSerialNo(), changes);
        checkChange("vendorId", null, requestDTO.getVendorId(), changes);
        checkChange("vendorUserId", null, requestDTO.getVendorUserId(), changes);
        checkChange("parentTicketId", null, requestDTO.getParentTicketId(), changes);
        checkChange("deviceModelId", null, requestDTO.getDeviceModelId(), changes);
        checkChange("brandId", null, requestDTO.getBrandId(), changes);
        checkChange("customModelName", null, requestDTO.getCustomModelName(), changes);

        TicketLogs log = new TicketLogs();
        log.setTicket(savedTicket);
        log.setModificationDate(LocalDateTime.now());
        log.setOldStatus(null);
        log.setNewStatus(savedTicket.getTicketStatus());
        log.setAssignorEmployee(null);
        log.setAssigneeEmployee(savedTicket.getEmployee());
        log.setAssignorRemarks(buildCreationRemarks(requestDTO));
        resolveModifiedBy(log, requestDTO);

        try {
            String changedFieldsJson = changes.isEmpty() ? null : objectMapper.writeValueAsString(changes);
            log.setChangedFields(changedFieldsJson);
        } catch (Exception e) {
            // Should not happen with Map serialization
        }

        savedTicket.setModNo(1);
        ticketLogsDao.save(log);
    }

    private String buildCreationRemarks(TicketRequestDTO requestDTO) {
        String remarks = requestDTO.getRemarks();
        return SecurityUtils.getCurrentUser()
                .filter(user -> "ROLE_VENDOR".equals(user.getAuthorities().iterator().next().getAuthority()))
                .map(user -> {
                    String prefix = "Created by vendor: " + user.getName();
                    if (remarks != null && !remarks.trim().isEmpty()) {
                        return prefix + " | " + remarks.trim();
                    }
                    return prefix;
                })
                .orElse(remarks);
    }

    private void resolveModifiedBy(TicketLogs log, TicketRequestDTO requestDTO) {
        Integer employeeId = requestDTO.getModifiedByEmployeeId();
        if (employeeId == null) {
            employeeId = StatusAccessValidator.getCurrentEmployeeId();
        }
        if (employeeId != null) {
            employeeDao.findById(employeeId).ifPresent(log::setModifiedBy);
        }
    }
}

