package com.mays.srm.service.impl;

import com.mays.srm.dao.core.DeviceDao;
import com.mays.srm.dao.core.StatusDao;
import com.mays.srm.dao.core.TicketDao;
import com.mays.srm.dto.TicketPatchDTO;
import com.mays.srm.entity.Device;
import com.mays.srm.entity.Status;
import com.mays.srm.entity.Ticket;
import com.mays.srm.service.TicketService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketDao ticketDao;
    private final DeviceDao deviceDao;
    private final StatusDao statusDao;

    @Autowired
    public TicketServiceImpl(TicketDao ticketDao, DeviceDao deviceDao, StatusDao statusDao) {
        this.ticketDao = ticketDao;
        this.deviceDao = deviceDao;
        this.statusDao = statusDao;
    }

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
                 // Existing device, let's fetch it fully to return a complete response
                 ticket.setDevice(existingDevice.get());
            } else {
                 // New device is being created along with the ticket
                 Device savedDevice = deviceDao.save(ticket.getDevice());
                 ticket.setDevice(savedDevice);
            }
        }
        return ticketDao.save(ticket);
    }

    @Override
    @Transactional
    public Ticket updateTicket(int id, TicketPatchDTO dto) {
        if (dto.getTicketStatus() != null) {
            Status status = new Status();
            status.setStatusId(dto.getTicketStatus());
            validateStatus(status);
        }
        return ticketDao.updateTicket(id, dto);
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
