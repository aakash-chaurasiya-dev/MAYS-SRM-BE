package com.mays.srm.service.impl;
import com.mays.srm.dao.core.TicketLogsDao;
import com.mays.srm.ticket.dto.resDTO.TicketLogsResponseDTO;
import com.mays.srm.ticket.dto.resDTO.TicketLogsSummaryResponseDTO;
import com.mays.srm.ticket.entities.TicketLogs;
import com.mays.srm.service.TicketLogsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketLogsServiceImpl implements TicketLogsService {

    private final TicketLogsDao repository;
    private final ModelMapper modelMapper;

    @Autowired
    public TicketLogsServiceImpl(TicketLogsDao repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<TicketLogsResponseDTO> getLogsForTicket(Integer ticketId) {
        List<TicketLogs> logList = repository.getDetailedTicketLogs(ticketId);
        List<TicketLogsResponseDTO> dtoList = new ArrayList<>();
        for (TicketLogs log : logList) {
            dtoList.add(mapToResponseDTO(log));
        }
        return dtoList;
    }

    @Override
    public List<TicketLogsSummaryResponseDTO> getLatestLogsForTicket(Integer ticketId) {
        return repository.getTicketLogsForTicketDetail(ticketId);
    }

    
    private TicketLogsResponseDTO mapToResponseDTO(TicketLogs log) {
        TicketLogsResponseDTO dto = modelMapper.map(log, TicketLogsResponseDTO.class);
        if (log.getTicket() != null) {
            dto.setTicketId(log.getTicket().getTicketId());
        }
        if (log.getAssignorEmployee() != null) {
            dto.setAssignorEmployeeName(log.getAssignorEmployee().getEmployeeName());
        }
        if (log.getAssigneeEmployee() != null) {
            dto.setAssigneeEmployeeName(log.getAssigneeEmployee().getEmployeeName());
        }
        if (log.getOldStatus() != null) {
            dto.setOldStatus(log.getOldStatus().getStatusName());
        }
        if (log.getNewStatus() != null) {
            dto.setNewStatus(log.getNewStatus().getStatusName());
        }
        if (log.getModifiedBy() != null) {
            dto.setModifiedBy(log.getModifiedBy().getEmployeeName());
        }
        dto.setChangedFields(log.getChangedFields());
        return dto;
    }
}
