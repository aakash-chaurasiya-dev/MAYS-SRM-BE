package com.mays.srm.service.impl;

import com.mays.srm.dao.core.*;
import com.mays.srm.dto.requestDTO.TicketRequestDTO;
import com.mays.srm.dto.responseDTO.TicketResponseDTO;
import com.mays.srm.entity.*;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.TicketService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketDao repository;
    private final UserMasterDao userMasterDao;
    private final TicketTypeDao ticketTypeDao;
    private final StatusDao statusDao;
    private final DeviceDao deviceDao;
    private final BranchDao branchDao;
    private final EmployeeDao employeeDao;
    private final TicketLogsDao ticketLogsDao;
    private final DeviceModelDao deviceModelDao;
    private final ModelMapper modelMapper;
    private final BillingDao billingDao;
    private final ChargeTypeDao chargeTypeDao;

    @Autowired
    public TicketServiceImpl(TicketDao repository, UserMasterDao userMasterDao, TicketTypeDao ticketTypeDao, StatusDao statusDao, DeviceDao deviceDao, BranchDao branchDao, EmployeeDao employeeDao, TicketLogsDao ticketLogsDao, DeviceModelDao deviceModelDao, ModelMapper modelMapper, BillingDao billingDao, ChargeTypeDao chargeTypeDao) {
        this.repository = repository;
        this.userMasterDao = userMasterDao;
        this.ticketTypeDao = ticketTypeDao;
        this.statusDao = statusDao;
        this.deviceDao = deviceDao;
        this.branchDao = branchDao;
        this.employeeDao = employeeDao;
        this.ticketLogsDao = ticketLogsDao;
        this.deviceModelDao = deviceModelDao;
        this.modelMapper = modelMapper;
        this.billingDao = billingDao;
        this.chargeTypeDao = chargeTypeDao;
    }

    @Override
    @Transactional
    public TicketResponseDTO create(TicketRequestDTO requestDTO) {
        try {
            Ticket ticket = new Ticket();
            
            // Set relationships from IDs
            validateAndSetRelations(ticket, requestDTO);

            // Handle device creation if a new serial number is provided
            handleDeviceCreation(ticket, requestDTO);

            // Map remaining fields
            modelMapper.map(requestDTO, ticket);
            
            Ticket savedTicket = repository.save(ticket);
            ensureFinalChargeExists(savedTicket);
            return mapToResponseDTO(savedTicket);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Ticket", ex);
        }
    }

    @Override
    public TicketResponseDTO getById(Integer id) {
        Optional<Ticket> ticketOpt = repository.findById(id);
        if (ticketOpt.isPresent()) {
            return mapToResponseDTO(ticketOpt.get());
        } else {
            throw new ResourceNotFoundException("Ticket not found with ID: " + id);
        }
    }

    @Override
    public List<TicketResponseDTO> getAll() {
        List<Ticket> ticketList = repository.findAll();
        List<TicketResponseDTO> dtoList = new ArrayList<>();
        for (Ticket ticket : ticketList) {
            dtoList.add(mapToResponseDTO(ticket));
        }
        return dtoList;
    }

    @Override
    @Transactional
    public TicketResponseDTO update(Integer id, TicketRequestDTO requestDTO) {
        Optional<Ticket> ticketOpt = repository.findById(id);
        if (ticketOpt.isEmpty()) {
            throw new ResourceNotFoundException("Ticket not found with ID: " + id);
        }
        Ticket ticket = ticketOpt.get();

        // 1. Audit Logging Logic (from old updateTicket)
        boolean statusChanged = requestDTO.getTicketStatusId() != null && 
                                (ticket.getTicketStatus() == null || !Objects.equals(ticket.getTicketStatus().getStatusId(), requestDTO.getTicketStatusId()));
        boolean employeeChanged = requestDTO.getEmployeeId() != null && 
                                  (ticket.getEmployee() == null || !Objects.equals(ticket.getEmployee().getEmployeeId(), requestDTO.getEmployeeId()));

        if (statusChanged || employeeChanged) {
            TicketLogs log = new TicketLogs();
            log.setTicket(ticket);
            log.setModificationDate(LocalDateTime.now());
            log.setAssignorRemarks(requestDTO.getRemarks());
            log.setOldStatus(ticket.getTicketStatus() != null ? ticket.getTicketStatus().getStatusName() : "N/A");
            log.setAssignorEmployee(ticket.getEmployee());

            if (statusChanged) {
                Status newStatus = statusDao.findById(requestDTO.getTicketStatusId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid status ID"));
                ticket.setTicketStatus(newStatus);
                log.setNewStatus(newStatus.getStatusName());
            } else {
                log.setNewStatus(log.getOldStatus());
            }

            if (employeeChanged) {
                Employee newAssignee = employeeDao.findById(requestDTO.getEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("New assignee employee not found with ID: " + requestDTO.getEmployeeId()));
                ticket.setEmployee(newAssignee);
                log.setAssigneeEmployee(newAssignee);
            } else {
                log.setAssigneeEmployee(ticket.getEmployee());
            }

            ticket.setModNo(ticket.getModNo() + 1);
            ticketLogsDao.save(log);
        }

        // 2. Handle Priority
        if (StringUtils.hasText(requestDTO.getPriority())) {
            ticket.setPriority(requestDTO.getPriority());
        }

        // 3. Update the rest of the ticket fields
        // We only want to map fields that are provided (not null) to act like a PATCH
        // We'll manually update other fields instead of ModelMapper to avoid overwriting existing data with nulls
        if (requestDTO.getTicketDescription() != null) {
            ticket.setTicketDescription(requestDTO.getTicketDescription());
        }
        if (requestDTO.getEmailId() != null) {
            ticket.setEmailId(requestDTO.getEmailId());
        }
        if (requestDTO.getWarrantyType() != null) {
            ticket.setWarrantyType(requestDTO.getWarrantyType());
        }
        
        // Re-validate and set complex relations if they were provided
        validateAndSetRelations(ticket, requestDTO);
        handleDeviceCreation(ticket, requestDTO);

        Ticket updatedTicket = repository.save(ticket);
        ensureFinalChargeExists(updatedTicket);
        return mapToResponseDTO(updatedTicket);
    }

    @Override
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
    
    // --- Custom Find Methods ---

    @Override
    public List<TicketResponseDTO> getAllTicketsOfUser(Integer userId) {
        List<Ticket> ticketList = repository.findByUserMasterUserId(userId);
        List<TicketResponseDTO> dtoList = new ArrayList<>();
        for (Ticket ticket : ticketList) {
            dtoList.add(mapToResponseDTO(ticket));
        }
        return dtoList;
    }

    @Override
    public List<TicketResponseDTO> getAllTicketsOfBranch(int branchId) {
        List<Ticket> ticketList = repository.findByTicketBranchBranchId(branchId);
        List<TicketResponseDTO> dtoList = new ArrayList<>();
        for (Ticket ticket : ticketList) {
            dtoList.add(mapToResponseDTO(ticket));
        }
        return dtoList;
    }

    @Override
    public List<TicketResponseDTO> getAllTicketsOfStatus(int statusId) {
        List<Ticket> ticketList = repository.findByTicketStatusStatusId(statusId);
        List<TicketResponseDTO> dtoList = new ArrayList<>();
        for (Ticket ticket : ticketList) {
            dtoList.add(mapToResponseDTO(ticket));
        }
        return dtoList;
    }

    @Override
    public List<TicketResponseDTO> getAllTicketsOfEmployee(int employeeId) {
        List<Ticket> ticketList = repository.findByEmployeeEmployeeId(employeeId);
        List<TicketResponseDTO> dtoList = new ArrayList<>();
        for (Ticket ticket : ticketList) {
            dtoList.add(mapToResponseDTO(ticket));
        }
        return dtoList;
    }

    // --- Helper Methods ---

    private void ensureFinalChargeExists(Ticket ticket) {
        Optional<Billing> existingCharge = billingDao.getChargeByTicketIdAndChargeName(ticket.getTicketId(), "Final Charge");
        if (existingCharge.isEmpty()) {
            Billing finalCharge = new Billing();
            finalCharge.setTicket(ticket);
            
            ChargeType chargeType = chargeTypeDao.getChargeTypeByName("Final Charge")
                .orElseGet(() -> {
                    ChargeType newChargeType = new ChargeType();
                    newChargeType.setChargeName("Final Charge");
                    newChargeType.setChargeDescription("Total Final Charge for the Ticket");
                    return chargeTypeDao.save(newChargeType);
                });
            finalCharge.setChargeType(chargeType);
            
            Status pendingStatus = statusDao.getStatusByNameAndType("Pending", "Billing")
                .orElseGet(() -> {
                    Status newStatus = new Status();
                    newStatus.setStatusName("Pending");
                    newStatus.setStatusType("Billing");
                    newStatus.setStatusDescription("Pending Billing Status");
                    return statusDao.save(newStatus);
                });
            finalCharge.setStatus(pendingStatus);
            
            finalCharge.setAmount(java.math.BigDecimal.ZERO);
            finalCharge.setBillingDate(LocalDateTime.now());
            
            billingDao.save(finalCharge);
        }
    }

    private void validateAndSetRelations(Ticket ticket, TicketRequestDTO requestDTO) {
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
        
        // Status and Employee are handled directly in the update method for logging purposes,
        // but we handle them here for the 'create' method
        if (requestDTO.getTicketStatusId() != null) {
            Optional<Status> statusOpt = statusDao.findById(requestDTO.getTicketStatusId());
            if (statusOpt.isPresent()) {
                if ("TICKET".equalsIgnoreCase(statusOpt.get().getStatusType())) {
                    ticket.setTicketStatus(statusOpt.get());
                }
                else {
                    throw new ResourceNotFoundException("Status has to be of type ticket: " + requestDTO.getTicketStatusId());
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

    private void handleDeviceCreation(Ticket ticket, TicketRequestDTO requestDTO) {
        if (requestDTO.getDeviceSerialNo() != null) {
            Optional<Device> deviceOpt = deviceDao.findById(requestDTO.getDeviceSerialNo());
            if (deviceOpt.isPresent()) {
                ticket.setDevice(deviceOpt.get());
            } else {
                // Create a new device if it doesn't exist
                Device newDevice = new Device();
                newDevice.setSerialNo(requestDTO.getDeviceSerialNo());
                if (requestDTO.getDeviceModelId() != null) {
                    Optional<DeviceModel> modelOpt = deviceModelDao.findById(requestDTO.getDeviceModelId());
                    if (modelOpt.isPresent()) {
                        newDevice.setModel(modelOpt.get());
                    } else {
                        throw new ResourceNotFoundException("Device Model not found with ID: " + requestDTO.getDeviceModelId());
                    }
                }
                ticket.setDevice(deviceDao.save(newDevice));
            }
        }
    }

    private TicketResponseDTO mapToResponseDTO(Ticket ticket) {
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
}
