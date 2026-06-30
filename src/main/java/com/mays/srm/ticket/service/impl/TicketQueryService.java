package com.mays.srm.ticket.service.impl;
import com.mays.srm.ticket.repository.TicketDao;
import com.mays.srm.ticket.dto.resDTO.TicketResponseDTO;
import com.mays.srm.ticket.dto.resDTO.TicketDashboardTicketStatsResponseDTO;
import com.mays.srm.ticket.dto.resDTO.TicketDashboardResponseDTO;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;

@Service
public class TicketQueryService {

    private final TicketDao ticketDao;
    private final TicketMapperService mapperService;

    @Autowired
    public TicketQueryService(TicketDao ticketDao, TicketMapperService mapperService) {
        this.ticketDao = ticketDao;
        this.mapperService = mapperService;
    }

    // Get individual Ticket for Ticket detail page
    public TicketResponseDTO getById(Integer id) {
        Optional<Ticket> ticketOpt = ticketDao.findById(id);
        if (ticketOpt.isPresent()) {
            return mapperService.mapToResponseDTO(ticketOpt.get());
        } else {
            throw new ResourceNotFoundException("Ticket not found with ID: " + id);
        }
    }

    // Get all tickets for Report full Detail of Ticket
    public List<TicketResponseDTO> getAll() {
        List<Ticket> ticketList = ticketDao.findAll();
        List<TicketResponseDTO> dtoList = new ArrayList<>();
        for (Ticket ticket : ticketList) {
            dtoList.add(mapperService.mapToResponseDTO(ticket));
        }
        return dtoList;
    }

    // get All tickets for Dashboard No mapper required
    public Page<TicketDashboardResponseDTO> getTicketsForDashboard(@NonNull Pageable pageable) {
        return ticketDao.getAllTicketDashboard(pageable);
    }

    public TicketDashboardTicketStatsResponseDTO getDashboardTicketStats() {
        return ticketDao.getDashboardTicketStats();
    }

    public Page<TicketDashboardResponseDTO> getTicketsByDepartmentDashboard(String departmentName, Pageable pageable) {
        return ticketDao.getTicketsByDepartmentDashboard(departmentName, pageable);
    }

    /**
     * Gets all tickets associated with a user
     */
    public List<TicketResponseDTO> getAllTicketsOfUser(Integer userId) {
        List<Ticket> ticketList = ticketDao.findByUserMasterUserId(userId);
        List<TicketResponseDTO> dtoList = new ArrayList<>();
        for (Ticket ticket : ticketList) {
            dtoList.add(mapperService.mapToResponseDTO(ticket));
        }
        return dtoList;
    }

    /**
     * Gets all tickets for a specific branch
     */
    public List<TicketResponseDTO> getAllTicketsOfBranch(int branchId) {
        List<Ticket> ticketList = ticketDao.findByTicketBranchBranchId(branchId);
        List<TicketResponseDTO> dtoList = new ArrayList<>();
        for (Ticket ticket : ticketList) {
            dtoList.add(mapperService.mapToResponseDTO(ticket));
        }
        return dtoList;
    }

    /**
     * Gets all tickets for a specific status
     */
    public List<TicketResponseDTO> getAllTicketsOfStatus(int statusId) {
        List<Ticket> ticketList = ticketDao.findByTicketStatusStatusId(statusId);
        List<TicketResponseDTO> dtoList = new ArrayList<>();
        for (Ticket ticket : ticketList) {
            dtoList.add(mapperService.mapToResponseDTO(ticket));
        }
        return dtoList;
    }

    /**
     * Gets all tickets assigned to a specific employee
     */
    public List<TicketResponseDTO> getAllTicketsOfEmployee(int employeeId) {
        List<Ticket> ticketList = ticketDao.findByEmployeeEmployeeId(employeeId);
        List<TicketResponseDTO> dtoList = new ArrayList<>();
        for (Ticket ticket : ticketList) {
            dtoList.add(mapperService.mapToResponseDTO(ticket));
        }
        return dtoList;
    }
}

