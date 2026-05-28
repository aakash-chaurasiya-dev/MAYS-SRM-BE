package com.mays.srm.service.impl;

import com.mays.srm.dao.core.*;
import com.mays.srm.dto.TicketPatchDTO;
import com.mays.srm.entity.*;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.TicketService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketDao ticketDao;
    private final DeviceDao deviceDao;
    private final StatusDao statusDao;
    private final TicketLogsDao ticketLogsDao;
    private final EmployeeDao employeeDao; // Added EmployeeDao

    @Autowired
    public TicketServiceImpl(TicketDao ticketDao, DeviceDao deviceDao, StatusDao statusDao, TicketLogsDao ticketLogsDao, EmployeeDao employeeDao) {
        this.ticketDao = ticketDao;
        this.deviceDao = deviceDao;
        this.statusDao = statusDao;
        this.ticketLogsDao = ticketLogsDao;
        this.employeeDao = employeeDao; // Injected EmployeeDao
    }

    @Override
    @Transactional
    public Ticket updateTicket(int id, TicketPatchDTO dto) {
        Ticket ticket = ticketDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + id));

        boolean statusChanged = dto.getTicketStatus() != null && !Objects.equals(ticket.getTicketStatus().getStatusId(), dto.getTicketStatus());
        boolean employeeChanged = dto.getEmployeeId() != null && !Objects.equals(ticket.getEmployee().getEmployeeId(), dto.getEmployeeId());

        // Only create a log if status or employee has changed
        if (statusChanged || employeeChanged) {
            TicketLogs log = new TicketLogs();
            log.setTicket(ticket);
            log.setModificationDate(LocalDateTime.now());
            log.setAssignorRemarks(dto.getRemarks());

            // Set original values for the log
            log.setOldStatus(ticket.getTicketStatus() != null ? ticket.getTicketStatus().getStatusName() : "N/A");
            log.setAssignorEmployee(ticket.getEmployee());

            // Handle Status Change
            if (statusChanged) {
                Status newStatus = statusDao.findById(dto.getTicketStatus())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid status ID"));
                validateStatus(newStatus);
                ticket.setTicketStatus(newStatus);
                log.setNewStatus(newStatus.getStatusName());
            } else {
                log.setNewStatus(log.getOldStatus()); // Status did not change
            }

            // Handle Employee Change
            if (employeeChanged) {
                Employee newAssignee = employeeDao.findById(dto.getEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("New assignee employee not found with ID: " + dto.getEmployeeId()));
                ticket.setEmployee(newAssignee);
                log.setAssigneeEmployee(newAssignee);
            } else {
                log.setAssigneeEmployee(ticket.getEmployee()); // Employee did not change, assignee is the same
            }

            // Increment modification number and save the log
            ticket.setModNo(ticket.getModNo() + 1);
            ticketLogsDao.save(log);
        }

        // Handle priority update (outside of the main logging logic)
        if (StringUtils.hasText(dto.getPriority())) {
            ticket.setPriority(dto.getPriority());
        }

        return ticketDao.save(ticket);
    }
    
    // --- Other methods remain unchanged ---

    @Override
    public Optional<Ticket> getTicket(int id) {
        return ticketDao.findById(id);
    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketDao.findAll();
    }

    @Override
    public List<Ticket> getAllTicketsOfUser(String userId) {
        return ticketDao.findByUserMasterUserId(userId);
    }

    @Override
    public List<Ticket> getAllTicketsOfBranch(int branchId) {
        return ticketDao.findByTicketBranchBranchId(branchId);
    }

    @Override
    public List<Ticket> getAllTicketsOfStatus(int statusId) {
        return ticketDao.findByTicketStatusStatusId(statusId);
    }

    @Override
    public List<Ticket> getAllTicketsOfEmployee(int employeeId) {
        return ticketDao.findByEmployeeEmployeeId(employeeId);
    }

    @Override
    @Transactional
    public Ticket createTicket(Ticket ticket) {
        validateStatus(ticket.getTicketStatus());
        if (ticket.getDevice() != null && ticket.getDevice().getSerialNo() != null) {
            Optional<Device> existingDevice = deviceDao.findById(ticket.getDevice().getSerialNo());
            if (existingDevice.isPresent()) {
                 ticket.setDevice(existingDevice.get());
            } else {
                 Device savedDevice = deviceDao.save(ticket.getDevice());
                 ticket.setDevice(savedDevice);
            }
        }
        return ticketDao.save(ticket);
    }

    @Override
    public void deleteTicket(int id) {
        ticketDao.deleteById(id);
    }

    private void validateStatus(Status status) {
        if (status != null && status.getStatusId() != null) {
            Status dbStatus = statusDao.findById(status.getStatusId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid status ID"));
            if (!"ticket".equalsIgnoreCase(dbStatus.getStatusType())) {
                throw new IllegalArgumentException("Status must be of type 'ticket'");
            }
        }
    }
}
