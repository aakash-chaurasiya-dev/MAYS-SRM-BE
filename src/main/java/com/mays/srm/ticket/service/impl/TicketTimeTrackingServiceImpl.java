package com.mays.srm.ticket.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mays.srm.ticket.dto.resDTO.TicketTimeTrackingResponseDTO;
import com.mays.srm.ticket.dto.resDTO.EmployeeTicketStatsDTO;
import com.mays.srm.ticket.dto.resDTO.EmployeeTicketHistoryDTO;
import com.mays.srm.ticket.entities.TicketTimeTracking;
import com.mays.srm.ticket.repository.TicketTimeTrackingRepository;
import com.mays.srm.ticket.service.TicketTimeTrackingService;
import com.mays.srm.ticket.repository.TicketLogsDao;
import com.mays.srm.ticket.entities.TicketLogs;
import org.springframework.data.domain.Page;
import com.mays.srm.util.RestPageImpl;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Objects;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.organization.entities.Status;
import com.mays.srm.user.entities.Employee;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.exception.InternalServerException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TicketTimeTrackingServiceImpl implements TicketTimeTrackingService {

    @Autowired
    private TicketTimeTrackingRepository trackingRepository;

    @Autowired
    private TicketLogsDao ticketLogsDao;

    @Override
    public List<TicketTimeTrackingResponseDTO> getTimeTrackingByTicketId(Integer ticketId) {
        try {
            List<TicketTimeTracking> records = trackingRepository.findByTicketTicketId(ticketId);
            List<TicketTimeTrackingResponseDTO> dtoList = new java.util.ArrayList<>();
            for (TicketTimeTracking record : records) {
                dtoList.add(mapToDTO(record));
            }
            return dtoList;
        } catch (Exception ex) {
            log.error("Error fetching time tracking by ticket id: {}", ticketId, ex);
            throw new InternalServerException("Error fetching time tracking records", ex);
        }
    }

    @Override
    public TicketTimeTrackingResponseDTO getTrackingById(Long id) {
        TicketTimeTracking record = trackingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Time tracking record not found with id: " + id));
        return mapToDTO(record);
    }

    private TicketTimeTrackingResponseDTO mapToDTO(TicketTimeTracking entity) {
        TicketTimeTrackingResponseDTO dto = new TicketTimeTrackingResponseDTO();
        dto.setId(entity.getId());
        if (entity.getTicket() != null) {
            dto.setTicketId(entity.getTicket().getTicketId());
        }
        if (entity.getAssignee() != null) {
            dto.setAssigneeId(entity.getAssignee().getEmployeeId());
            dto.setAssigneeName(entity.getAssignee().getEmployeeName());
        }
        dto.setAssignedAt(entity.getAssignedAt());
        dto.setReleasedAt(entity.getReleasedAt());
        dto.setAccumulatedMinutes(entity.getAccumulatedMinutes());
        dto.setLastClockStart(entity.getLastClockStart());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }

    @Override
    public void startTrackingForNewTicket(Ticket ticket) {
        try {
            if (ticket.getEmployee() != null) {
                TicketTimeTracking tracking = new TicketTimeTracking();
                tracking.setTicket(ticket);
                tracking.setAssignee(ticket.getEmployee());
                tracking.setAssignedAt(LocalDateTime.now());
                tracking.setLastClockStart(LocalDateTime.now());
                tracking.setAccumulatedMinutes(0);
                tracking.setIsActive(true);
                trackingRepository.save(tracking);
                log.info("Started time tracking for ticket id: {}", ticket.getTicketId());
            }
        } catch (Exception ex) {
            log.error("Error starting time tracking for ticket id: {}", ticket.getTicketId(), ex);
            throw new InternalServerException("Error starting time tracking", ex);
        }
    }

    @Override
    public void handleTrackingUpdates(Ticket updatedTicket, Status oldStatus, Employee oldAssignee) {
        try {
            // 1. Assignee changes
            Employee newAssignee = updatedTicket.getEmployee();
            Integer oldAssigneeId = oldAssignee != null ? oldAssignee.getEmployeeId() : null;
            Integer newAssigneeId = newAssignee != null ? newAssignee.getEmployeeId() : null;

            boolean assigneeChanged = !Objects.equals(oldAssigneeId, newAssigneeId);

            // 2. Status changes
            Status newStatus = updatedTicket.getTicketStatus();
            Integer oldStatusId = oldStatus != null ? oldStatus.getStatusId() : null;
            Integer newStatusId = newStatus != null ? newStatus.getStatusId() : null;
            
            boolean statusChanged = !Objects.equals(oldStatusId, newStatusId);

            if (assigneeChanged) {
                // Stop old tracker if exists
                stopActiveTracker(updatedTicket);
                // Start new tracker for new assignee
                if (newAssignee != null) {
                    System.out.println("We are coming here");
                    startTrackingForNewTicket(updatedTicket);
                }
            }

            // Apply status constraints to the active tracker
            if (newStatus != null && "TICKET".equalsIgnoreCase(newStatus.getStatusType())) {
                String statusName = newStatus.getStatusName();
                if (statusName != null) {
                    if (statusName.equalsIgnoreCase("HOLD")) {
                        pauseActiveTracker(updatedTicket);
                    } else if (statusName.equalsIgnoreCase("CLOSED")) {
                        stopActiveTracker(updatedTicket);
                    } else if (statusChanged) {
                        // It changed from HOLD to something active
                        resumeActiveTracker(updatedTicket);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Error handling time tracking updates for ticket id: {}", updatedTicket.getTicketId(), ex);
            throw new InternalServerException("Error handling time tracking updates", ex);
        }
    }

    private void stopActiveTracker(Ticket ticket) {
        try {
            List<TicketTimeTracking> activeTrackers = trackingRepository.findByTicketTicketIdAndIsActiveTrue(ticket.getTicketId());
            for (TicketTimeTracking tracker : activeTrackers) {
                tracker.setIsActive(false);
                tracker.setReleasedAt(LocalDateTime.now());
                if (tracker.getLastClockStart() != null) {
                    long minutes = Duration.between(tracker.getLastClockStart(), LocalDateTime.now()).toMinutes();
                    tracker.setAccumulatedMinutes(tracker.getAccumulatedMinutes() + (int) minutes);
                    tracker.setLastClockStart(null);
                }
                trackingRepository.save(tracker);
                log.info("Stopped tracking for ticket id: {}", ticket.getTicketId());
            }
        } catch (Exception ex) {
            log.error("Error stopping active tracker for ticket id: {}", ticket.getTicketId(), ex);
            throw new InternalServerException("Error stopping time tracking", ex);
        }
    }

    private void pauseActiveTracker(Ticket ticket) {
        try {
            List<TicketTimeTracking> activeTrackers = trackingRepository.findByTicketTicketIdAndIsActiveTrue(ticket.getTicketId());
            for (TicketTimeTracking tracker : activeTrackers) {
                if (tracker.getLastClockStart() != null) {
                    long minutes = Duration.between(tracker.getLastClockStart(), LocalDateTime.now()).toMinutes();
                    tracker.setAccumulatedMinutes(tracker.getAccumulatedMinutes() + (int) minutes);
                    tracker.setLastClockStart(null);
                    trackingRepository.save(tracker);
                    log.info("Paused tracking for ticket id: {}", ticket.getTicketId());
                }
            }
        } catch (Exception ex) {
            log.error("Error pausing active tracker for ticket id: {}", ticket.getTicketId(), ex);
            throw new InternalServerException("Error pausing time tracking", ex);
        }
    }

    private void resumeActiveTracker(Ticket ticket) {
        try {
            List<TicketTimeTracking> activeTrackers = trackingRepository.findByTicketTicketIdAndIsActiveTrue(ticket.getTicketId());
            for (TicketTimeTracking tracker : activeTrackers) {
                if (tracker.getLastClockStart() == null) {
                    tracker.setLastClockStart(LocalDateTime.now());
                    trackingRepository.save(tracker);
                    log.info("Resumed tracking for ticket id: {}", ticket.getTicketId());
                }
            }
        } catch (Exception ex) {
            log.error("Error resuming active tracker for ticket id: {}", ticket.getTicketId(), ex);
            throw new InternalServerException("Error resuming time tracking", ex);
        }
    }

    @Override
    public EmployeeTicketStatsDTO getEmployeeTicketStats(Integer employeeId) {
        try {
            List<TicketTimeTracking> records = trackingRepository.findByAssigneeEmployeeId(employeeId);
            
            Map<Integer, Boolean> ticketActiveMap = new HashMap<>();
            
            for (TicketTimeTracking record : records) {
                Integer tId = record.getTicket().getTicketId();
                boolean isActive = record.getIsActive() != null && record.getIsActive();
                ticketActiveMap.put(tId, ticketActiveMap.getOrDefault(tId, false) || isActive);
            }
            
            int total = ticketActiveMap.size();
            int open = 0;
            int closed = 0;
            
            for (Boolean isActive : ticketActiveMap.values()) {
                if (isActive) {
                    open++;
                } else {
                    closed++;
                }
            }
            
            EmployeeTicketStatsDTO stats = new EmployeeTicketStatsDTO();
            stats.setTotalTickets(total);
            stats.setOpenTickets(open);
            stats.setClosedTickets(closed);
            
            return stats;
        } catch (Exception ex) {
            log.error("Error fetching ticket stats for employee id: {}", employeeId, ex);
            throw new InternalServerException("Error fetching employee ticket stats", ex);
        }
    }

    @Override
    public Page<EmployeeTicketHistoryDTO> getEmployeeTicketHistory(Integer employeeId, Pageable pageable) {
        try {
            List<TicketTimeTracking> records = trackingRepository.findByAssigneeEmployeeId(employeeId);
            
            Map<Integer, List<TicketTimeTracking>> ticketRecordsMap = records.stream()
                .collect(Collectors.groupingBy(r -> r.getTicket().getTicketId()));
                
            List<EmployeeTicketHistoryDTO> historyList = new ArrayList<>();
            
            for (Map.Entry<Integer, List<TicketTimeTracking>> entry : ticketRecordsMap.entrySet()) {
                List<TicketTimeTracking> tRecords = entry.getValue();
                Ticket ticket = tRecords.get(0).getTicket();
                
                double totalMinutes = 0;
                for (TicketTimeTracking tr : tRecords) {
                    totalMinutes += tr.getAccumulatedMinutes() != null ? tr.getAccumulatedMinutes() : 0;
                    if (tr.getIsActive() != null && tr.getIsActive() && tr.getLastClockStart() != null) {
                        long activeMinutes = Duration.between(tr.getLastClockStart(), LocalDateTime.now()).toMinutes();
                        totalMinutes += activeMinutes;
                    }
                }
                
                EmployeeTicketHistoryDTO dto = new EmployeeTicketHistoryDTO();
                dto.setTicketId(ticket.getTicketId());
                dto.setEmployeeName(tRecords.get(0).getAssignee().getEmployeeName());
                if (ticket.getUserMaster() != null) {
                    String firstName = ticket.getUserMaster().getFirstName() != null ? ticket.getUserMaster().getFirstName() : "";
                    String lastName = ticket.getUserMaster().getLastName() != null ? ticket.getUserMaster().getLastName() : "";
                    dto.setUserName((firstName + " " + lastName).trim());
                }
                dto.setHoursSpent(Math.round((totalMinutes / 60.0) * 100.0) / 100.0);
                dto.setCreatedDate(ticket.getCreatedDate());
                dto.setSlaDate(ticket.getTargetDate());
                
                List<TicketLogs> logs = ticketLogsDao.findByTicketTicketId(ticket.getTicketId());
                logs.sort(Comparator.comparing(TicketLogs::getModificationDate).reversed());
                
                for (TicketLogs tLog : logs) {
                    if (tLog.getAssignorEmployee() != null && tLog.getAssignorEmployee().getEmployeeId().equals(employeeId)) {
                        if (tLog.getAssignorRemarks() != null && !tLog.getAssignorRemarks().trim().isEmpty()) {
                            dto.setFinalRemark(tLog.getAssignorRemarks());
                            break;
                        }
                    }
                }
                
                historyList.add(dto);
            }
            
            // Sort historyList by createdDate desc
            historyList.sort(Comparator.comparing(EmployeeTicketHistoryDTO::getCreatedDate, Comparator.nullsLast(Comparator.reverseOrder())));
            
            // Apply pagination manually
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), historyList.size());
            List<EmployeeTicketHistoryDTO> pageContent;
            if (start <= end) {
                pageContent = historyList.subList(start, end);
            } else {
                pageContent = new ArrayList<>();
            }
            
            return new RestPageImpl<>(pageContent, pageable, historyList.size());
            
        } catch (Exception ex) {
            log.error("Error fetching ticket history for employee id: {}", employeeId, ex);
            throw new InternalServerException("Error fetching employee ticket history", ex);
        }
    }
}
