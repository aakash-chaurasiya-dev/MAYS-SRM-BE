package com.mays.srm.service.impl.TicketServiceImplementations;

import com.mays.srm.dto.responseDTO.TicketDTO.TicketResponseDTO;
import com.mays.srm.dto.responseDTO.TicketDTO.TicketDashboardResponseDTO;
import com.mays.srm.entity.Ticket;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketMapperService {

    private final ModelMapper modelMapper;

    @Autowired
    public TicketMapperService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * Maps a Ticket entity to the standard TicketResponseDTO
     */
    public TicketResponseDTO mapToResponseDTO(Ticket ticket) {
        TicketResponseDTO dto = modelMapper.map(ticket, TicketResponseDTO.class);

        if (ticket.getUserMaster() != null) {
            dto.setUserFirstName(ticket.getUserMaster().getFirstName());
            dto.setUserLastName(ticket.getUserMaster().getLastName());
            dto.setUserMobileNo(ticket.getUserMaster().getMobileNo());
        }
        if (ticket.getTicketType() != null) {
            dto.setTicketTypeName(ticket.getTicketType().getTicketTypeName());
        }
        if (ticket.getTicketStatus() != null) {
            dto.setTicketStatusName(ticket.getTicketStatus().getStatusName());
        }
        if (ticket.getDevice() != null) {
            dto.setDeviceSerialNo(ticket.getDevice().getSerialNo());
            if (ticket.getDevice().getModel() != null) {
                dto.setDeviceModelName(ticket.getDevice().getModel().getModelName());
                if (ticket.getDevice().getModel().getBrand() != null) {
                    dto.setDeviceBrandName(ticket.getDevice().getModel().getBrand().getBrandName());
                    dto.setDeviceTypeName(ticket.getDevice().getModel().getBrand().getDeviceType().getDeviceTypeName());
                } else {
                    dto.setDeviceBrandName("N/A");
                    dto.setDeviceTypeName("N/A");
                }
            }
        }

        if (ticket.getTicketBranch() != null) {
            dto.setBranchName(ticket.getTicketBranch().getBranchName());
        }
        if (ticket.getEmployee() != null) {
            dto.setEmployeeName(ticket.getEmployee().getEmployeeName());
            if (ticket.getEmployee().getDepartment() != null) {
                dto.setDepartmentName(ticket.getEmployee().getDepartment().getDepartmentName());
            }
        }

        return dto;
    }

    /**
     * Maps a Ticket entity to the Dashboard specific DTO
     */
    public TicketDashboardResponseDTO mapToDashboardResponseDTO(Ticket ticket) {
        TicketDashboardResponseDTO dto = modelMapper.map(ticket, TicketDashboardResponseDTO.class);

        if (ticket.getUserMaster() != null) {
            dto.setUserFirstName(ticket.getUserMaster().getFirstName());
            dto.setUserLastName(ticket.getUserMaster().getLastName());
        }

        if (ticket.getDevice() != null) {
            dto.setDeviceSerialNo(ticket.getDevice().getSerialNo());
        }

        if (ticket.getTicketBranch() != null) {
            dto.setBranchName(ticket.getTicketBranch().getBranchName());
        }

        if (ticket.getTicketStatus() != null) {
            dto.setTicketStatusName(ticket.getTicketStatus().getStatusName());
        }

        if (ticket.getEmployee() != null) {
            if (ticket.getEmployee().getDepartment() != null) {
                dto.setDepartmentName(ticket.getEmployee().getDepartment().getDepartmentName());
            }
        }
        return dto;
    }
}
