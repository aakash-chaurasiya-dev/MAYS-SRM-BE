package com.mays.srm.ticket.service.impl;

import com.mays.srm.ticket.repository.TicketDao;
import com.mays.srm.ticket.dto.request.TicketRequestDTO;
import com.mays.srm.ticket.dto.resDTO.TicketDashboardTicketStatsResponseDTO;
import com.mays.srm.ticket.dto.resDTO.TicketDashboardResponseDTO;
import com.mays.srm.ticket.dto.resDTO.TicketResponseDTO;
import com.mays.srm.ticket.dto.resDTO.TicketUserDashboardResponseDTO;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.ticket.service.TicketAccessoriesService;
import com.mays.srm.ticket.service.TicketService;
import com.mays.srm.ticket.service.TicketTimeTrackingService;
import com.mays.srm.notification.service.NotificationService;
import com.mays.srm.organization.entities.Status;
import com.mays.srm.user.entities.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

// This is Redies Changes: Import Spring Cache Annotations
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

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
    private final TicketAccessoriesService ticketAccessoriesService;
    private final TicketTimeTrackingService ticketTimeTrackingService;
    private final NotificationService notificationService;

    @Autowired
    public TicketServiceImpl(TicketDao repository,
            TicketQueryService ticketQueryService,
            TicketMapperService ticketMapperService,
            TicketDeviceService ticketDeviceService,
            TicketValidationService ticketValidationService,
            TicketAuditService ticketAuditService,
            TicketBillingService ticketBillingService,
            TicketAccessoriesService ticketAccessoriesService,
            TicketTimeTrackingService ticketTimeTrackingService,
            NotificationService notificationService) {
        this.repository = repository;
        this.ticketQueryService = ticketQueryService;
        this.ticketMapperService = ticketMapperService;
        this.ticketDeviceService = ticketDeviceService;
        this.ticketValidationService = ticketValidationService;
        this.ticketAuditService = ticketAuditService;
        this.ticketBillingService = ticketBillingService;
        this.ticketAccessoriesService = ticketAccessoriesService;
        this.ticketTimeTrackingService = ticketTimeTrackingService;
        this.notificationService = notificationService;
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
            if (requestDTO.getReferredCategoryDecriptionTicket() != null) {
                ticket.setReferredCategoryDecriptionTicket(requestDTO.getReferredCategoryDecriptionTicket());
            }
            if (requestDTO.getPriority() != null) {
                ticket.setPriority(requestDTO.getPriority());
            }
            if (requestDTO.getTargetDate() != null) {
                ticket.setTargetDate(requestDTO.getTargetDate());
            }
            if (requestDTO.getClosedDate() != null) {
                ticket.setClosedDate(requestDTO.getClosedDate());
            }

            Ticket savedTicket = repository.save(ticket);
            ticketBillingService.ensureFinalChargeExists(savedTicket);

            // Handle Time Tracking for new ticket
            ticketTimeTrackingService.startTrackingForNewTicket(savedTicket);

            if (requestDTO.getAccessoryIds() != null) {
                ticketAccessoriesService.syncAccessories(savedTicket, requestDTO.getAccessoryIds());
            }

            // Enqueue notification email
            if (savedTicket.getEmailId() != null && !savedTicket.getEmailId().isEmpty()) {
                java.util.Map<String, Object> variables = new java.util.HashMap<>();
                variables.put("ticketNo", savedTicket.getTicketId());
                variables.put("status", savedTicket.getTicketStatus() != null ? savedTicket.getTicketStatus().getStatusName() : "Created");
                variables.put("description", savedTicket.getTicketDescription());
                variables.put("userName", savedTicket.getUserMaster().getFirstName() != null ? savedTicket.getUserMaster().getFirstName() : "Customer");
                variables.put("company_name", "Mays Computer Repair & Solutions");
                notificationService.enqueueEmail(savedTicket.getEmailId(), "Ticket Created: " + savedTicket.getTicketId(), "ticket-notification", variables);
            }

            return ticketMapperService.mapToResponseDTO(savedTicket);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Ticket", ex);
        }
    }

    // This is Redies Changes: Cache the ticket response using Redis
    @Override
    @Cacheable(value = "tickets", key = "#id")
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
    @Cacheable(value = "tickets", key = "'dashboardStats'")
    public TicketDashboardTicketStatsResponseDTO getDashboardTicketStats() {
        return ticketQueryService.getDashboardTicketStats();
    }

    @Override
    public Page<TicketDashboardResponseDTO> getTicketsByDepartmentDashboard(String departmentName, Pageable pageable) {
        return ticketQueryService.getTicketsByDepartmentDashboard(departmentName, pageable);
    }
    
    @Override
    public List<TicketUserDashboardResponseDTO> getLightweightTicketsByUserId(Integer userId) {
        return ticketQueryService.getLightweightTicketsByUserId(userId);
    }

    // This is Redies Changes: Remove ticket from cache when it is updated
    @Override
    @Transactional
    @CacheEvict(value = "tickets", key = "#id")
    public TicketResponseDTO update(Integer id, TicketRequestDTO requestDTO) {
        Optional<Ticket> ticketOpt = repository.findById(id);
        if (ticketOpt.isEmpty()) {
            throw new ResourceNotFoundException("Ticket not found with ID: " + id);
        }
        Ticket ticket = ticketOpt.get();

        // Capture old status and old assignee for time tracking before they are overwritten
        Status oldStatus = ticket.getTicketStatus();
        Employee oldAssignee = ticket.getEmployee();

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
        if (requestDTO.getReferredCategoryDecriptionTicket() != null) {
            ticket.setReferredCategoryDecriptionTicket(requestDTO.getReferredCategoryDecriptionTicket());
        }
        if (requestDTO.getTargetDate() != null) {
            ticket.setTargetDate(requestDTO.getTargetDate());
        }
        if (requestDTO.getClosedDate() != null) {
            ticket.setClosedDate(requestDTO.getClosedDate());
        }

        // Re-validate and set complex relations if they were provided
        ticketValidationService.validateAndSetRelations(ticket, requestDTO);
        ticketDeviceService.handleDeviceCreation(ticket, requestDTO);

        Ticket updatedTicket = repository.save(ticket);
        ticketBillingService.ensureFinalChargeExists(updatedTicket);

        // Handle Time Tracking updates
        System.out.println("are we coming here 009");
        ticketTimeTrackingService.handleTrackingUpdates(updatedTicket, oldStatus, oldAssignee);

        if (requestDTO.getAccessoryIds() != null) {
            ticketAccessoriesService.syncAccessories(updatedTicket, requestDTO.getAccessoryIds());
        }

        // Enqueue notification email
        if (updatedTicket.getEmailId() != null && !updatedTicket.getEmailId().isEmpty()) {
            java.util.Map<String, Object> variables = new java.util.HashMap<>();
            variables.put("ticketNo", updatedTicket.getTicketId());
            variables.put("status", updatedTicket.getTicketStatus() != null ? updatedTicket.getTicketStatus().getStatusName() : "Updated");
            variables.put("description", updatedTicket.getTicketDescription());
            notificationService.enqueueEmail(updatedTicket.getEmailId(), "Ticket Updated: " + updatedTicket.getTicketId(), "ticket-notification", variables);
        }

        return ticketMapperService.mapToResponseDTO(updatedTicket);
    }

    // This is Redies Changes: Remove ticket from cache when deleted
    @Override
    @CacheEvict(value = "tickets", key = "#id")
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

    @Override
    public List<TicketResponseDTO> getAllTicketsOfVendor(Integer vendorId) {
        return ticketQueryService.getAllTicketsOfVendor(vendorId);
    }
}
