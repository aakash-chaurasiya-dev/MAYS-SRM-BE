package com.mays.srm.ticket.service.impl;
import com.mays.srm.organization.repository.BranchDao;
import com.mays.srm.organization.repository.StatusDao;
import com.mays.srm.ticket.repository.TicketTypeDao;
import com.mays.srm.ticket.dto.request.TicketRequestDTO;
import com.mays.srm.organization.entities.Branch;
import com.mays.srm.user.entities.Employee;
import com.mays.srm.organization.entities.Status;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.ticket.entities.TicketType;
import com.mays.srm.user.entities.UserMaster;
import com.mays.srm.user.repository.EmployeeDao;
import com.mays.srm.user.repository.UserMasterDao;
import com.mays.srm.user.repository.VendorDao;
import com.mays.srm.user.repository.VendorUserDao;
import com.mays.srm.ticket.repository.TicketDao;
import com.mays.srm.user.entities.Vendor;
import com.mays.srm.user.entities.VendorUser;
import com.mays.srm.ticket.entities.ReferredCategory;
import com.mays.srm.ticket.entities.WarrantyType;
import com.mays.srm.ticket.repository.ReferredCategoryDao;
import com.mays.srm.ticket.repository.WarrantyTypeDao;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.exception.BadRequestException;
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
    private final VendorDao vendorDao;
    private final VendorUserDao vendorUserDao;
    private final TicketDao ticketDao;
    private final ReferredCategoryDao referredCategoryDao;
    private final WarrantyTypeDao warrantyTypeDao;

    @Autowired
    public TicketValidationService(UserMasterDao userMasterDao, TicketTypeDao ticketTypeDao,
                                   StatusDao statusDao, BranchDao branchDao, EmployeeDao employeeDao,
                                   VendorDao vendorDao, VendorUserDao vendorUserDao, TicketDao ticketDao,
                                   ReferredCategoryDao referredCategoryDao, WarrantyTypeDao warrantyTypeDao) {
        this.userMasterDao = userMasterDao;
        this.ticketTypeDao = ticketTypeDao;
        this.statusDao = statusDao;
        this.branchDao = branchDao;
        this.employeeDao = employeeDao;
        this.vendorDao = vendorDao;
        this.vendorUserDao = vendorUserDao;
        this.ticketDao = ticketDao;
        this.referredCategoryDao = referredCategoryDao;
        this.warrantyTypeDao = warrantyTypeDao;
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

        if (requestDTO.getVendorId() != null) {
            Optional<Vendor> vendorOpt = vendorDao.findById(requestDTO.getVendorId());
            if (vendorOpt.isPresent()) {
                ticket.setVendor(vendorOpt.get());
            } else {
                throw new ResourceNotFoundException("Vendor not found with ID: " + requestDTO.getVendorId());
            }
        }

        if (requestDTO.getVendorUserId() != null) {
            Optional<VendorUser> vuOpt = vendorUserDao.findById(requestDTO.getVendorUserId());
            if (vuOpt.isPresent()) {
                VendorUser vu = vuOpt.get();
                // Validate that this vendor user belongs to the selected vendor
                Vendor currentVendor = ticket.getVendor();
                if (currentVendor != null && !currentVendor.getId().equals(vu.getVendor().getId())) {
                    throw new BadRequestException("Vendor User ID " + requestDTO.getVendorUserId() + 
                            " does not belong to Vendor ID " + currentVendor.getId());
                }
                ticket.setVendorUser(vu);
            } else {
                throw new ResourceNotFoundException("Vendor User not found with ID: " + requestDTO.getVendorUserId());
            }
        }

        if (requestDTO.getParentTicketId() != null) {
            Optional<Ticket> parentOpt = ticketDao.findById(requestDTO.getParentTicketId());
            if (parentOpt.isPresent()) {
                ticket.setParentTicket(parentOpt.get());
            } else {
                throw new ResourceNotFoundException("Parent Ticket not found with ID: " + requestDTO.getParentTicketId());
            }
        }

        if (requestDTO.getReferredCategoryId() != null) {
            Optional<ReferredCategory> rcOpt = referredCategoryDao.findById(requestDTO.getReferredCategoryId());
            if (rcOpt.isPresent()) {
                ticket.setReferredCategory(rcOpt.get());
            } else {
                throw new ResourceNotFoundException("Referred Category not found with ID: " + requestDTO.getReferredCategoryId());
            }
        }

        if (requestDTO.getWarrantyTypeId() != null) {
            Optional<WarrantyType> wtOpt = warrantyTypeDao.findById(requestDTO.getWarrantyTypeId());
            if (wtOpt.isPresent()) {
                ticket.setWarrantyType(wtOpt.get());
            } else {
                throw new ResourceNotFoundException("Warranty Type not found with ID: " + requestDTO.getWarrantyTypeId());
            }
        }
    }
}

