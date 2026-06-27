package com.mays.srm.service.impl.TicketServiceImplementations;

import com.mays.srm.dao.core.BranchDao;
import com.mays.srm.dao.core.EmployeeDao;
import com.mays.srm.dao.core.StatusDao;
import com.mays.srm.dao.core.TicketTypeDao;
import com.mays.srm.dao.core.UserMasterDao;
import com.mays.srm.dto.requestDTO.TicketDTO.TicketRequestDTO;
import com.mays.srm.entity.Branch;
import com.mays.srm.entity.Employee;
import com.mays.srm.entity.Status;
import com.mays.srm.entity.Ticket;
import com.mays.srm.entity.TicketType;
import com.mays.srm.entity.UserMaster;
import com.mays.srm.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TicketValidationService {

    private final UserMasterDao userMasterDao;
    private final TicketTypeDao ticketTypeDao;
    private final StatusDao statusDao;
    private final BranchDao branchDao;
    private final EmployeeDao employeeDao;

    @Autowired
    public TicketValidationService(UserMasterDao userMasterDao, TicketTypeDao ticketTypeDao,
                                   StatusDao statusDao, BranchDao branchDao, EmployeeDao employeeDao) {
        this.userMasterDao = userMasterDao;
        this.ticketTypeDao = ticketTypeDao;
        this.statusDao = statusDao;
        this.branchDao = branchDao;
        this.employeeDao = employeeDao;
    }

    /**
     * Validates and sets complex relations on the Ticket entity based on IDs from the request
     */
    public void validateAndSetRelations(Ticket ticket, TicketRequestDTO requestDTO) {
        if (requestDTO.getUserRefNo() != null) {
            Optional<UserMaster> userOpt = userMasterDao.findById(Integer.parseInt(requestDTO.getUserRefNo()));
            if (userOpt.isPresent()) {
                ticket.setUserMaster(userOpt.get());
            } else {
                throw new ResourceNotFoundException("User not found with ID: " + requestDTO.getUserRefNo());
            }
        }

        if (requestDTO.getTicketTypeId() != null) {
            Optional<TicketType> typeOpt = ticketTypeDao.findById(requestDTO.getTicketTypeId());
            if (typeOpt.isPresent()) {
                ticket.setTicketType(typeOpt.get());
            } else {
                throw new ResourceNotFoundException("Ticket Type not found with ID: " + requestDTO.getTicketTypeId());
            }
        }

        if (requestDTO.getTicketStatusId() != null) {
            Optional<Status> statusOpt = statusDao.findById(requestDTO.getTicketStatusId());
            if (statusOpt.isPresent()) {
                if ("TICKET".equalsIgnoreCase(statusOpt.get().getStatusType())) {
                    ticket.setTicketStatus(statusOpt.get());
                } else {
                    throw new ResourceNotFoundException(
                            "Status has to be of type ticket: " + requestDTO.getTicketStatusId());
                }
            } else {
                throw new ResourceNotFoundException("Status not found with ID: " + requestDTO.getTicketStatusId());
            }
        }

        if (requestDTO.getTicketBranchId() != null) {
            Optional<Branch> branchOpt = branchDao.findById(requestDTO.getTicketBranchId());
            if (branchOpt.isPresent()) {
                ticket.setTicketBranch(branchOpt.get());
            } else {
                throw new ResourceNotFoundException("Branch not found with ID: " + requestDTO.getTicketBranchId());
            }
        }

        if (requestDTO.getEmployeeId() != null) {
            Optional<Employee> empOpt = employeeDao.findById(requestDTO.getEmployeeId());
            if (empOpt.isPresent()) {
                ticket.setEmployee(empOpt.get());
            } else {
                throw new ResourceNotFoundException("Employee not found with ID: " + requestDTO.getEmployeeId());
            }
        }
    }
}
