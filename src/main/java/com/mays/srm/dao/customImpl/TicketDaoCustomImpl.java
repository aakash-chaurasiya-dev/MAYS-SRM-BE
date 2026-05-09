package com.mays.srm.dao.customImpl;

import com.mays.srm.dao.custom.TicketDaoCustom;
import com.mays.srm.dto.TicketPatchDTO;
import com.mays.srm.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

public class TicketDaoCustomImpl implements TicketDaoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Ticket updateTicket(int id, TicketPatchDTO dto) {
        Ticket ticket = entityManager.find(Ticket.class, id);
        
        if (ticket == null || dto == null) {
            return ticket;
        }

        if (dto.getUserRefNo() != null) {
            UserMaster user = new UserMaster();
            user.setUserId(dto.getUserRefNo());
            ticket.setUserMaster(user);
        }

        if (dto.getTicketType() != null) {
            TicketType type = new TicketType();
            type.setTicketTypeId(dto.getTicketType());
            ticket.setTicketType(type);
        }

        if (dto.getTicketStatus() != null) {
            Status status = new Status();
            status.setStatusId(dto.getTicketStatus());
            ticket.setTicketStatus(status);
        }

        if (dto.getEmailId() != null) {
            ticket.setEmailId(dto.getEmailId());
        }

        if (dto.getSerialNo() != null) {
            Device device = new Device();
            device.setSerialNo(dto.getSerialNo());
            ticket.setDevice(device);
        }

        if (dto.getTicketDescription() != null) {
            ticket.setTicketDescription(dto.getTicketDescription());
        }

        if (dto.getTicketBranch() != null) {
            Branch branch = new Branch();
            branch.setBranchId(dto.getTicketBranch());
            ticket.setTicketBranch(branch);
        }

        if (dto.getEmployeeId() != null) {
            Employee employee = new Employee();
            employee.setEmployeeId(dto.getEmployeeId());
            ticket.setEmployee(employee);
        }

        if (dto.getWarrantyType() != null) {
            ticket.setWarrantyType(dto.getWarrantyType());
        }

        entityManager.merge(ticket);
        return ticket;
    }
}
