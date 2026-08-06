package com.mays.srm.timetracking.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mays.srm.timetracking.dto.resDTO.SlaHoldRequestResponseDTO;
import com.mays.srm.timetracking.entities.SlaHoldRequest;
import com.mays.srm.timetracking.enums.HoldRequestStatus;
import com.mays.srm.timetracking.repository.SlaHoldRequestRepository;
import com.mays.srm.timetracking.service.SlaHoldRequestService;

@Service
public class SlaHoldRequestServiceImpl implements SlaHoldRequestService {

    @Autowired
    private SlaHoldRequestRepository holdRequestRepository;

    @Override
    public List<SlaHoldRequestResponseDTO> getPendingRequests() {
        return holdRequestRepository.findByStatusOrderByRequestedAtAsc(HoldRequestStatus.PENDING)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SlaHoldRequestResponseDTO> getByTicketId(Integer ticketId) {
        return holdRequestRepository.findByTicketTicketIdOrderByRequestedAtDesc(ticketId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SlaHoldRequestResponseDTO getActiveForTicket(Integer ticketId) {
        return holdRequestRepository
                .findFirstByTicketTicketIdAndStatusOrderByRequestedAtDesc(ticketId, HoldRequestStatus.PENDING)
                .map(this::mapToDTO)
                .or(() -> holdRequestRepository
                        .findFirstByTicketTicketIdAndStatusOrderByRequestedAtDesc(ticketId, HoldRequestStatus.APPROVED)
                        .map(this::mapToDTO))
                .orElse(null);
    }

    private SlaHoldRequestResponseDTO mapToDTO(SlaHoldRequest entity) {
        SlaHoldRequestResponseDTO dto = new SlaHoldRequestResponseDTO();
        dto.setId(entity.getId());
        if (entity.getTicket() != null) {
            dto.setTicketId(entity.getTicket().getTicketId());
            if (entity.getTicket().getTicketStatus() != null) {
                dto.setTicketStatusName(entity.getTicket().getTicketStatus().getStatusName());
            }
            if (entity.getTicket().getEmployee() != null) {
                dto.setAssigneeName(entity.getTicket().getEmployee().getEmployeeName());
            }
        }
        if (entity.getTracking() != null) {
            dto.setTrackingId(entity.getTracking().getId());
        }
        if (entity.getRequestedBy() != null) {
            dto.setRequestedById(entity.getRequestedBy().getEmployeeId());
            dto.setRequestedByName(entity.getRequestedBy().getEmployeeName());
        }
        dto.setReason(entity.getReason());
        dto.setStatus(entity.getStatus());
        dto.setRequestedAt(entity.getRequestedAt());
        if (entity.getReviewedBy() != null) {
            dto.setReviewedById(entity.getReviewedBy().getEmployeeId());
            dto.setReviewedByName(entity.getReviewedBy().getEmployeeName());
        }
        dto.setReviewedAt(entity.getReviewedAt());
        dto.setReviewRemark(entity.getReviewRemark());
        dto.setReleasedAt(entity.getReleasedAt());
        return dto;
    }
}
