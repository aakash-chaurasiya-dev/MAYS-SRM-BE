package com.mays.srm.enquiry.service.impl;

import com.mays.srm.enquiry.dto.request.OutwardRequestDTO;
import com.mays.srm.enquiry.dto.resDTO.OutwardResponseDTO;
import com.mays.srm.enquiry.entities.OutwardRecord;
import com.mays.srm.enquiry.repository.OutwardRecordDao;
import com.mays.srm.enquiry.service.OutwardService;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.exception.BadRequestException;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.ticket.repository.TicketDao;
import com.mays.srm.user.entities.UserEntryReport;
import com.mays.srm.user.entities.UserMaster;
import com.mays.srm.user.repository.UserEntryReportDao;
import com.mays.srm.organization.entities.Status;
import com.mays.srm.organization.repository.StatusDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OutwardServiceImpl implements OutwardService {

    @Autowired
    private OutwardRecordDao repository;

    @Autowired
    private TicketDao ticketDao;

    @Autowired
    private StatusDao statusDao;

    @Autowired
    private UserEntryReportDao userEntryReportDao;

    @Autowired
    private com.mays.srm.ticket.service.TicketService ticketService;

    @Override
    @Transactional
    public OutwardResponseDTO createOutward(OutwardRequestDTO requestDTO) {
        // 1. Resolve Ticket
        Ticket ticket = ticketDao.findById(requestDTO.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + requestDTO.getTicketId()));

        // 2. Validate Duplicate Outward Handover
        Optional<OutwardRecord> existingOutward = repository.findByTicketTicketId(requestDTO.getTicketId());
        if (existingOutward.isPresent()) {
            throw new BadRequestException("Device for Ticket ID: " + requestDTO.getTicketId() + 
                    " was already outwarded on: " + existingOutward.get().getCreatedDate());
        }

        UserMaster customer = ticket.getUserMaster();
        if (customer == null) {
            throw new BadRequestException("Ticket is not associated with a valid customer.");
        }

        String serialNo = ticket.getDevice() != null ? ticket.getDevice().getSerialNo() : null;
        if (serialNo == null) {
            throw new BadRequestException("Ticket is not associated with a valid device serial number.");
        }

        // 3. Save OutwardRecord
        OutwardRecord outward = new OutwardRecord();
        outward.setTicket(ticket);
        outward.setUser(customer);
        outward.setSerialNo(serialNo);
        outward.setOutwardRemarks(requestDTO.getOutwardRemarks());
        outward.setHandoverToName(requestDTO.getHandoverToName());
        outward.setHandoverToPhone(requestDTO.getHandoverToPhone());
        outward.setCreatedByEmployeeId(requestDTO.getCreatedByEmployeeId());

        OutwardRecord savedOutward = repository.save(outward);

        // 4. Update Ticket Status to Delivered/Closed
        Optional<Status> closedStatusOpt = statusDao.getStatusByNameAndType("Delivered", "TICKET");
        if (closedStatusOpt.isEmpty()) {
            closedStatusOpt = statusDao.getStatusByNameAndType("Closed", "TICKET");
        }
        if (closedStatusOpt.isEmpty()) {
            closedStatusOpt = statusDao.getStatusByNameAndType("Outwarded", "TICKET");
        }

        if (closedStatusOpt.isPresent()) {
            ticket.setTicketStatus(closedStatusOpt.get());
            ticket.setClosedDate(LocalDateTime.now());
            ticketDao.save(ticket);
        }

        // 5. Log UserEntryReport automatically
        UserEntryReport report = new UserEntryReport();
        report.setUser(customer);
        report.setReason("Outward");
        report.setEntryType("OUTWARD");
        report.setTicket(ticket);
        report.setOutward(savedOutward);
        userEntryReportDao.save(report);

        return mapToResponse(savedOutward);
    }

    @Override
    public List<OutwardResponseDTO> getAllOutwards() {
        return repository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public OutwardResponseDTO getOutwardById(Integer id) {
        Object record = null; // not used, keeping compile safe
        OutwardRecord recordOut = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Outward record not found with ID: " + id));
        return mapToResponse(recordOut);
    }

    @Override
    public OutwardResponseDTO getOutwardByTicketId(Integer ticketId) {
        OutwardRecord record = repository.findByTicketTicketId(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Outward record not found for Ticket ID: " + ticketId));
        return mapToResponse(record);
    }

    @Override
    public List<com.mays.srm.ticket.dto.resDTO.TicketResponseDTO> getEligibleTicketsForUser(Integer userId) {
        List<com.mays.srm.ticket.dto.resDTO.TicketResponseDTO> tickets = ticketService.getAllTicketsOfUser(userId);
        return tickets.stream()
                .filter(t -> !repository.findByTicketTicketId(t.getTicketId()).isPresent())
                .collect(Collectors.toList());
    }

    private OutwardResponseDTO mapToResponse(OutwardRecord record) {
        OutwardResponseDTO dto = new OutwardResponseDTO();
        dto.setOutwardId(record.getOutwardId());
        dto.setTicketId(record.getTicket().getTicketId());
        dto.setUserId(record.getUser().getUserId());
        dto.setUserName(record.getUser().getFirstName() + " " + record.getUser().getLastName());
        dto.setSerialNo(record.getSerialNo());
        dto.setOutwardStatus(record.getOutwardStatus());
        dto.setOutwardRemarks(record.getOutwardRemarks());
        dto.setHandoverToName(record.getHandoverToName());
        dto.setHandoverToPhone(record.getHandoverToPhone());
        dto.setCreatedByEmployeeId(record.getCreatedByEmployeeId());
        dto.setCreatedDate(record.getCreatedDate());
        return dto;
    }
}
