package com.mays.srm.ticket.service.impl;
import com.mays.srm.dao.core.TicketDao;
import com.mays.srm.ticket.dto.request.TicketRequestDTO;
import com.mays.srm.ticket.dto.resDTO.DashboardTicketStatsResponseDTO;
import com.mays.srm.ticket.dto.resDTO.TicketDashboardResponseDTO;
import com.mays.srm.ticket.dto.resDTO.TicketResponseDTO;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.ticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketDao repository;
    private final TicketQueryService ticketQueryService;
    private final TicketMapperService ticketMapperService;
    private final TicketDeviceService ticketDeviceService;
    private final TicketValidationService ticketValidationService;
    private final TicketAuditService ticketAuditService;
    private final TicketBillingService ticketBillingService;

    @Autowired
    public TicketServiceImpl(TicketDao repository,
                             TicketQueryService ticketQueryService,
                             TicketMapperService ticketMapperService,
                             TicketDeviceService ticketDeviceService,
                             TicketValidationService ticketValidationService,
                             TicketAuditService ticketAuditService,
                             TicketBillingService ticketBillingService) {
        this.repository = repository;
        this.ticketQueryService = ticketQueryService;
        this.ticketMapperService = ticketMapperService;
        this.ticketDeviceService = ticketDeviceService;
        this.ticketValidationService = ticketValidationService;
        this.ticketAuditService = ticketAuditService;
        this.ticketBillingService = ticketBillingService;
    }

    @Override
    @Transactional
    public TicketResponseDTO create(TicketRequestDTO requestDTO) {
        try {
            Ticket ticket = new Ticket();

            // Set relationships from IDs
            ticketValidationService.validateAndSetRelations(ticket, requestDTO);

            // Handle device creation if a new serial number is provided
            ticketDeviceService.handleDeviceCreation(ticket, requestDTO);

            // Map remaining fields manually without ModelMapper
            if (requestDTO.getTicketDescription() != null) {
                ticket.setTicketDescription(requestDTO.getTicketDescription());
            }
            if (requestDTO.getEmailId() != null) {
                ticket.setEmailId(requestDTO.getEmailId());
            }
            if (requestDTO.getWarrantyType() != null) {
                ticket.setWarrantyType(requestDTO.getWarrantyType());
            }
            if (requestDTO.getPriority() != null) {
                ticket.setPriority(requestDTO.getPriority());
            }

            Ticket savedTicket = repository.save(ticket);
            ticketBillingService.ensureFinalChargeExists(savedTicket);

            return ticketMapperService.mapToResponseDTO(savedTicket);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Ticket", ex);
        }
    }

    @Override
    public TicketResponseDTO getById(Integer id) {
        return ticketQueryService.getById(id);
    }

    @Override
    public List<TicketResponseDTO> getAll() {
        return ticketQueryService.getAll();
    }

    @Override
    public Page<TicketDashboardResponseDTO> getTicketsForDashboard(Pageable pageable) {
        return ticketQueryService.getTicketsForDashboard(pageable);
    }

    @Override
    public DashboardTicketStatsResponseDTO getDashboardTicketStats() {
        return ticketQueryService.getDashboardTicketStats();
    }

    @Override
    public Page<TicketDashboardResponseDTO> getTicketsByDepartmentDashboard(String departmentName, Pageable pageable) {
        return ticketQueryService.getTicketsByDepartmentDashboard(departmentName, pageable);
    }

    @Override
    @Transactional
    public TicketResponseDTO update(Integer id, TicketRequestDTO requestDTO) {
        Optional<Ticket> ticketOpt = repository.findById(id);
        if (ticketOpt.isEmpty()) {
            throw new ResourceNotFoundException("Ticket not found with ID: " + id);
        }
        Ticket ticket = ticketOpt.get();

        // 1. Audit Logging Logic
        ticketAuditService.logChanges(ticket, requestDTO);

        // 2. Handle Priority
        if (StringUtils.hasText(requestDTO.getPriority())) {
            ticket.setPriority(requestDTO.getPriority());
        }

        // 3. Update the rest of the ticket fields
        if (requestDTO.getTicketDescription() != null) {
            ticket.setTicketDescription(requestDTO.getTicketDescription());
        }
        if (requestDTO.getEmailId() != null) {
            ticket.setEmailId(requestDTO.getEmailId());
        }
        if (requestDTO.getWarrantyType() != null) {
            ticket.setWarrantyType(requestDTO.getWarrantyType());
        }

        // Re-validate and set complex relations if they were provided
        ticketValidationService.validateAndSetRelations(ticket, requestDTO);
        ticketDeviceService.handleDeviceCreation(ticket, requestDTO);

        Ticket updatedTicket = repository.save(ticket);
        ticketBillingService.ensureFinalChargeExists(updatedTicket);
        return ticketMapperService.mapToResponseDTO(updatedTicket);
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Ticket not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Ticket because it has related records.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Ticket with ID: " + id, ex);
        }
    }

    // --- Custom Find Methods Delegated ---

    @Override
    public List<TicketResponseDTO> getAllTicketsOfUser(Integer userId) {
        return ticketQueryService.getAllTicketsOfUser(userId);
    }

    @Override
    public List<TicketResponseDTO> getAllTicketsOfBranch(int branchId) {
        return ticketQueryService.getAllTicketsOfBranch(branchId);
    }

    @Override
    public List<TicketResponseDTO> getAllTicketsOfStatus(int statusId) {
        return ticketQueryService.getAllTicketsOfStatus(statusId);
    }

    @Override
    public List<TicketResponseDTO> getAllTicketsOfEmployee(int employeeId) {
        return ticketQueryService.getAllTicketsOfEmployee(employeeId);
    }
}
