package com.mays.srm.timetracking.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.organization.entities.Status;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.ticket.entities.TicketLogs;
import com.mays.srm.ticket.repository.TicketLogsDao;
import com.mays.srm.timetracking.dto.resDTO.EmployeeTicketHistoryDTO;
import com.mays.srm.timetracking.dto.resDTO.EmployeeTicketStatsDTO;
import com.mays.srm.timetracking.dto.resDTO.TicketTimeTrackingResponseDTO;
import com.mays.srm.timetracking.entities.SlaHoldRequest;
import com.mays.srm.timetracking.entities.TicketTimeTracking;
import com.mays.srm.timetracking.enums.HoldRequestStatus;
import com.mays.srm.timetracking.enums.SlaTimerAction;
import com.mays.srm.timetracking.repository.SlaHoldRequestRepository;
import com.mays.srm.timetracking.repository.TicketTimeTrackingRepository;
import com.mays.srm.timetracking.service.SlaCalculationService;
import com.mays.srm.timetracking.service.TicketTimeTrackingService;
import com.mays.srm.timetracking.util.StatusAccessValidator;
import com.mays.srm.user.entities.Employee;
import com.mays.srm.user.repository.EmployeeDao;
import com.mays.srm.util.RestPageImpl;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TicketTimeTrackingServiceImpl implements TicketTimeTrackingService {

    @Autowired
    private TicketTimeTrackingRepository trackingRepository;

    @Autowired
    private SlaHoldRequestRepository holdRequestRepository;

    @Autowired
    private TicketLogsDao ticketLogsDao;

    @Autowired
    private SlaCalculationService slaCalculationService;

    @Autowired
    private EmployeeDao employeeDao;

    @Override
    public List<TicketTimeTrackingResponseDTO> getTimeTrackingByTicketId(Integer ticketId) {
        try {
            List<TicketTimeTracking> records = trackingRepository.findByTicketTicketId(ticketId);
            List<TicketTimeTrackingResponseDTO> dtoList = new ArrayList<>();
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

    private int computeTotalMinutes(TicketTimeTracking entity) {
        int total = entity.getAccumulatedMinutes() != null ? entity.getAccumulatedMinutes() : 0;
        if (Boolean.TRUE.equals(entity.getIsActive()) && entity.getLastClockStart() != null) {
            total += (int) Duration.between(entity.getLastClockStart(), LocalDateTime.now()).toMinutes();
        }
        return total;
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
        dto.setIsTimerPaused(Boolean.TRUE.equals(entity.getIsActive()) && entity.getLastClockStart() == null);

        if (entity.getAssignee() != null) {
            int targetMins = slaCalculationService.resolveTargetMinutes(entity.getAssignee());
            dto.setTargetMinutes(targetMins);
            dto.setIsCrossedTAT(computeTotalMinutes(entity) > targetMins);
        }

        return dto;
    }

    @Override
    public void startTrackingForNewTicket(Ticket ticket) {
        try {
            if (ticket.getEmployee() == null) {
                return;
            }
            if (!slaCalculationService.isTimerTracked(ticket.getEmployee())) {
                log.info("Skipping time tracking for non-tracked assignee on ticket id: {}", ticket.getTicketId());
                return;
            }
            TicketTimeTracking tracking = new TicketTimeTracking();
            tracking.setTicket(ticket);
            tracking.setAssignee(ticket.getEmployee());
            tracking.setAssignedAt(LocalDateTime.now());
            tracking.setLastClockStart(LocalDateTime.now());
            tracking.setAccumulatedMinutes(0);
            tracking.setIsActive(true);
            trackingRepository.save(tracking);
            log.info("Started time tracking for ticket id: {}", ticket.getTicketId());
        } catch (Exception ex) {
            log.error("Error starting time tracking for ticket id: {}", ticket.getTicketId(), ex);
            throw new InternalServerException("Error starting time tracking", ex);
        }
    }

    @Override
    public void handleTrackingUpdates(Ticket updatedTicket, Status oldStatus, Employee oldAssignee, String holdReason) {
        try {
            Employee newAssignee = updatedTicket.getEmployee();
            Integer oldAssigneeId = oldAssignee != null ? oldAssignee.getEmployeeId() : null;
            Integer newAssigneeId = newAssignee != null ? newAssignee.getEmployeeId() : null;
            boolean assigneeChanged = !Objects.equals(oldAssigneeId, newAssigneeId);

            Status newStatus = updatedTicket.getTicketStatus();
            Integer oldStatusId = oldStatus != null ? oldStatus.getStatusId() : null;
            Integer newStatusId = newStatus != null ? newStatus.getStatusId() : null;
            boolean statusChanged = !Objects.equals(oldStatusId, newStatusId);

            SlaTimerAction oldAction = resolveAction(oldStatus);
            SlaTimerAction newAction = resolveAction(newStatus);

            if (assigneeChanged) {
                stopActiveTracker(updatedTicket);
                if (newAssignee != null) {
                    startTrackingForNewTicket(updatedTicket);
                }
            }

            if (statusChanged) {
                handleLeavingHoldRequestStatus(updatedTicket, oldAction, newAction);
                applyStatusTimerAction(updatedTicket, newAction, holdReason);
            }
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error handling time tracking updates for ticket id: {}", updatedTicket.getTicketId(), ex);
            throw new InternalServerException("Error handling time tracking updates", ex);
        }
    }

    private SlaTimerAction resolveAction(Status status) {
        if (status == null || status.getSlaTimerAction() == null) {
            return SlaTimerAction.NONE;
        }
        return SlaTimerAction.fromString(status.getSlaTimerAction());
    }

    private void handleLeavingHoldRequestStatus(Ticket ticket, SlaTimerAction oldAction, SlaTimerAction newAction) {
        if (oldAction != SlaTimerAction.CREATE_HOLD_REQUEST) {
            return;
        }
        if (newAction == SlaTimerAction.PAUSE_TIMER) {
            return;
        }
        holdRequestRepository.findFirstByTicketTicketIdAndStatusOrderByRequestedAtDesc(
                ticket.getTicketId(), HoldRequestStatus.PENDING).ifPresent(request -> {
            if (StatusAccessValidator.isExecutiveOrManager()) {
                request.setStatus(HoldRequestStatus.REJECTED);
            } else {
                request.setStatus(HoldRequestStatus.CANCELLED);
            }
            request.setReviewedAt(LocalDateTime.now());
            Integer reviewerId = StatusAccessValidator.getCurrentEmployeeId();
            if (reviewerId != null) {
                employeeDao.findById(reviewerId).ifPresent(request::setReviewedBy);
            }
            holdRequestRepository.save(request);
            log.info("Hold request {} for ticket {}", request.getStatus(), ticket.getTicketId());
        });
    }

    private void applyStatusTimerAction(Ticket ticket, SlaTimerAction action, String holdReason) {
        switch (action) {
            case CREATE_HOLD_REQUEST -> createHoldRequest(ticket, holdReason);
            case PAUSE_TIMER -> {
                approvePendingHoldRequest(ticket);
                pauseActiveTracker(ticket);
            }
            case RESUME_TIMER -> {
                releaseApprovedHoldRequest(ticket);
                resumeActiveTracker(ticket);
            }
            case STOP_TIMER -> {
                closeOpenHoldRequests(ticket);
                stopActiveTracker(ticket);
            }
            default -> { }
        }
    }

    private void createHoldRequest(Ticket ticket, String holdReason) {
        holdRequestRepository.findFirstByTicketTicketIdAndStatusOrderByRequestedAtDesc(
                ticket.getTicketId(), HoldRequestStatus.PENDING).ifPresent(existing -> {
            throw new BadRequestException("A pending hold request already exists for this ticket.");
        });

        List<TicketTimeTracking> activeTrackers = trackingRepository.findByTicketTicketIdAndIsActiveTrue(ticket.getTicketId());
        if (activeTrackers.isEmpty()) {
            throw new BadRequestException("No active time tracking record found for this ticket.");
        }
        TicketTimeTracking tracker = activeTrackers.get(0);

        Integer requesterId = StatusAccessValidator.getCurrentEmployeeId();
        if (requesterId == null) {
            throw new BadRequestException("Employee context required to request SLA hold.");
        }
        Employee requester = employeeDao.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + requesterId));

        SlaHoldRequest request = new SlaHoldRequest();
        request.setTicket(ticket);
        request.setTracking(tracker);
        request.setRequestedBy(requester);
        request.setReason(holdReason != null && !holdReason.isBlank() ? holdReason.trim() : "SLA hold requested");
        request.setStatus(HoldRequestStatus.PENDING);
        request.setRequestedAt(LocalDateTime.now());
        holdRequestRepository.save(request);
        log.info("Created pending hold request for ticket id: {}", ticket.getTicketId());
    }

    private void approvePendingHoldRequest(Ticket ticket) {
        holdRequestRepository.findFirstByTicketTicketIdAndStatusOrderByRequestedAtDesc(
                ticket.getTicketId(), HoldRequestStatus.PENDING).ifPresent(request -> {
            request.setStatus(HoldRequestStatus.APPROVED);
            request.setReviewedAt(LocalDateTime.now());
            Integer reviewerId = StatusAccessValidator.getCurrentEmployeeId();
            if (reviewerId != null) {
                employeeDao.findById(reviewerId).ifPresent(request::setReviewedBy);
            }
            holdRequestRepository.save(request);
        });
    }

    private void releaseApprovedHoldRequest(Ticket ticket) {
        holdRequestRepository.findFirstByTicketTicketIdAndStatusOrderByRequestedAtDesc(
                ticket.getTicketId(), HoldRequestStatus.APPROVED).ifPresent(request -> {
            request.setStatus(HoldRequestStatus.RELEASED);
            request.setReleasedAt(LocalDateTime.now());
            request.setReviewedAt(LocalDateTime.now());
            Integer reviewerId = StatusAccessValidator.getCurrentEmployeeId();
            if (reviewerId != null) {
                employeeDao.findById(reviewerId).ifPresent(request::setReviewedBy);
            }
            holdRequestRepository.save(request);
        });
    }

    private void closeOpenHoldRequests(Ticket ticket) {
        for (HoldRequestStatus openStatus : List.of(HoldRequestStatus.PENDING, HoldRequestStatus.APPROVED)) {
            holdRequestRepository.findFirstByTicketTicketIdAndStatusOrderByRequestedAtDesc(
                    ticket.getTicketId(), openStatus).ifPresent(request -> {
                request.setStatus(HoldRequestStatus.RELEASED);
                request.setReleasedAt(LocalDateTime.now());
                holdRequestRepository.save(request);
            });
        }
    }

    private void stopActiveTracker(Ticket ticket) {
        List<TicketTimeTracking> activeTrackers = trackingRepository.findByTicketTicketIdAndIsActiveTrue(ticket.getTicketId());
        for (TicketTimeTracking tracker : activeTrackers) {
            tracker.setIsActive(false);
            tracker.setReleasedAt(LocalDateTime.now());
            flushElapsedMinutes(tracker);
            trackingRepository.save(tracker);
            log.info("Stopped tracking for ticket id: {}", ticket.getTicketId());
        }
    }

    private void pauseActiveTracker(Ticket ticket) {
        List<TicketTimeTracking> activeTrackers = trackingRepository.findByTicketTicketIdAndIsActiveTrue(ticket.getTicketId());
        for (TicketTimeTracking tracker : activeTrackers) {
            if (tracker.getLastClockStart() != null) {
                flushElapsedMinutes(tracker);
                tracker.setLastClockStart(null);
                trackingRepository.save(tracker);
                log.info("Paused tracking for ticket id: {}", ticket.getTicketId());
            }
        }
    }

    private void resumeActiveTracker(Ticket ticket) {
        List<TicketTimeTracking> activeTrackers = trackingRepository.findByTicketTicketIdAndIsActiveTrue(ticket.getTicketId());
        for (TicketTimeTracking tracker : activeTrackers) {
            if (tracker.getLastClockStart() == null) {
                tracker.setLastClockStart(LocalDateTime.now());
                trackingRepository.save(tracker);
                log.info("Resumed tracking for ticket id: {}", ticket.getTicketId());
            }
        }
    }

    private void flushElapsedMinutes(TicketTimeTracking tracker) {
        if (tracker.getLastClockStart() != null) {
            long minutes = Duration.between(tracker.getLastClockStart(), LocalDateTime.now()).toMinutes();
            int accumulated = tracker.getAccumulatedMinutes() != null ? tracker.getAccumulatedMinutes() : 0;
            tracker.setAccumulatedMinutes(accumulated + (int) minutes);
            tracker.setLastClockStart(null);
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
            stats.setTotalTickets(ticketActiveMap.size());
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
                    totalMinutes += computeTotalMinutes(tr);
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

                Employee assignee = tRecords.get(0).getAssignee();
                int targetMins = slaCalculationService.resolveTargetMinutes(assignee);
                dto.setTargetHours(Math.round((targetMins / 60.0) * 100.0) / 100.0);
                dto.setIsCrossedTAT((totalMinutes / 60.0) > (targetMins / 60.0));

                List<TicketLogs> logs = ticketLogsDao.findByTicketTicketId(ticket.getTicketId());
                logs.sort(Comparator.comparing(TicketLogs::getModificationDate).reversed());
                for (TicketLogs tLog : logs) {
                    if (tLog.getAssignorEmployee() != null
                            && tLog.getAssignorEmployee().getEmployeeId().equals(employeeId)) {
                        if (tLog.getAssignorRemarks() != null && !tLog.getAssignorRemarks().trim().isEmpty()) {
                            dto.setFinalRemark(tLog.getAssignorRemarks());
                            break;
                        }
                    }
                }
                historyList.add(dto);
            }

            historyList.sort(Comparator.comparing(EmployeeTicketHistoryDTO::getCreatedDate,
                    Comparator.nullsLast(Comparator.reverseOrder())));

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), historyList.size());
            List<EmployeeTicketHistoryDTO> pageContent = start <= end ? historyList.subList(start, end) : new ArrayList<>();
            return new RestPageImpl<>(pageContent, pageable, historyList.size());
        } catch (Exception ex) {
            log.error("Error fetching ticket history for employee id: {}", employeeId, ex);
            throw new InternalServerException("Error fetching employee ticket history", ex);
        }
    }
}
