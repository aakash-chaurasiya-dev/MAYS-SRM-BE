package com.mays.srm.enquiry.service.impl;

import com.mays.srm.enquiry.repository.EnquiryDao;
import com.mays.srm.organization.repository.StatusDao;
import com.mays.srm.enquiry.dto.request.EnquiryRequestDTO;
import com.mays.srm.enquiry.dto.resDTO.EnquiryPendingCountDTO;
import com.mays.srm.enquiry.dto.resDTO.EnquiryResponseDTO;
import com.mays.srm.device.entities.Brand;
import com.mays.srm.device.entities.DeviceModel;
import com.mays.srm.device.entities.DeviceType;
import com.mays.srm.device.repository.BrandDao;
import com.mays.srm.device.repository.DeviceModelDao;
import com.mays.srm.device.repository.DeviceTypeDao;
import com.mays.srm.enquiry.entities.Enquiry;
import com.mays.srm.organization.entities.Status;
import com.mays.srm.user.entities.UserMaster;
import com.mays.srm.user.repository.UserMasterDao;
import com.mays.srm.user.entities.UserEntryReport;
import com.mays.srm.user.repository.UserEntryReportDao;
import com.mays.srm.ticket.service.TicketService;
import com.mays.srm.ticket.repository.TicketTypeDao;
import com.mays.srm.ticket.dto.request.TicketRequestDTO;
import com.mays.srm.ticket.dto.resDTO.TicketResponseDTO;
import com.mays.srm.ticket.entities.TicketType;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.exception.BadRequestException;
import com.mays.srm.enquiry.service.EnquiryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EnquiryServiceImpl implements EnquiryService {

    private final EnquiryDao repository;
    private final UserMasterDao userMasterDao;
    private final BrandDao brandDao;
    private final StatusDao statusDao;
    private final DeviceTypeDao deviceTypeDao;
    private final DeviceModelDao deviceModelDao;
    private final UserEntryReportDao userEntryReportDao;
    private final TicketService ticketService;
    private final TicketTypeDao ticketTypeDao;
    private final ModelMapper modelMapper;

    @Autowired
    public EnquiryServiceImpl(EnquiryDao repository, UserMasterDao userMasterDao, BrandDao brandDao,
                              StatusDao statusDao, DeviceTypeDao deviceTypeDao, DeviceModelDao deviceModelDao,
                              UserEntryReportDao userEntryReportDao, TicketService ticketService,
                              TicketTypeDao ticketTypeDao, ModelMapper modelMapper) {
        this.repository = repository;
        this.userMasterDao = userMasterDao;
        this.brandDao = brandDao;
        this.statusDao = statusDao;
        this.deviceTypeDao = deviceTypeDao;
        this.deviceModelDao = deviceModelDao;
        this.userEntryReportDao = userEntryReportDao;
        this.ticketService = ticketService;
        this.ticketTypeDao = ticketTypeDao;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    @CacheEvict(value = "enquiries", allEntries = true)
    public EnquiryResponseDTO create(EnquiryRequestDTO requestDTO) {
        try {
            Enquiry enquiry = modelMapper.map(requestDTO, Enquiry.class);
            enquiry.setTimestamp(LocalDateTime.now());
            validateAndSetRelations(enquiry, requestDTO);
            
            Enquiry savedEnquiry = repository.save(enquiry);

            // Automatically log UserEntryReport
            if (savedEnquiry.getUser() != null) {
                UserEntryReport report = new UserEntryReport();
                report.setUser(savedEnquiry.getUser());
                report.setReason("Enquiry");
                report.setEntryType("ENQUIRY");
                report.setEnquiry(savedEnquiry);
                userEntryReportDao.save(report);
            }

            return mapToResponseDTO(savedEnquiry);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Enquiry", ex);
        }
    }

    @Override
    @Cacheable(value = "enquiries", key = "#id")
    public EnquiryResponseDTO getById(Integer id) {
        Optional<Enquiry> enquiryOpt = repository.findById(id);
        if (enquiryOpt.isPresent()) {
            return mapToResponseDTO(enquiryOpt.get());
        } else {
            throw new ResourceNotFoundException("Enquiry not found with ID: " + id);
        }
    }

    @Override
    @Cacheable(value = "enquiries", key = "'all'")
    public List<EnquiryResponseDTO> getAll() {
        List<Enquiry> enquiryList = repository.findAll();
        List<EnquiryResponseDTO> dtoList = new ArrayList<>();
        for (Enquiry enquiry : enquiryList) {
            dtoList.add(mapToResponseDTO(enquiry));
        }
        return dtoList;
    }

    @Override
    @Transactional
    @CacheEvict(value = "enquiries", allEntries = true)
    public EnquiryResponseDTO update(Integer id, EnquiryRequestDTO requestDTO) {
        Optional<Enquiry> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Enquiry not found with ID: " + id);
        }
        
        Enquiry existingEnquiry = existingOpt.get();
        modelMapper.map(requestDTO, existingEnquiry);
        existingEnquiry.setEnquiryId(id);

        try {
            validateAndSetRelations(existingEnquiry, requestDTO);
            Enquiry updatedEnquiry = repository.save(existingEnquiry);
            return mapToResponseDTO(updatedEnquiry);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Enquiry", ex);
        }
    }

    @Override
    @Cacheable(value = "enquiries", key = "'user-' + #userId")
    public List<EnquiryResponseDTO> getAllEnquiriesOfUser(Integer userId) {
        List<Enquiry> enquiryList = repository.findByUserUserId(userId);
        List<EnquiryResponseDTO> dtoList = new ArrayList<>();
        for (Enquiry enquiry : enquiryList) {
            dtoList.add(mapToResponseDTO(enquiry));
        }
        return dtoList;
    }

    @Override
    @Cacheable(value = "enquiries", key = "'pending-count-all'")
    public EnquiryPendingCountDTO getPendingCountAll() {
        return new EnquiryPendingCountDTO(repository.countPendingEnquiries());
    }

    @Override
    @Cacheable(value = "enquiries", key = "'pending-count-user-' + #userId")
    public EnquiryPendingCountDTO getPendingCountForUser(Integer userId) {
        return new EnquiryPendingCountDTO(repository.countPendingEnquiriesByUser(userId));
    }

    @Override
    @Transactional
    @CacheEvict(value = "enquiries", allEntries = true)
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Enquiry not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Enquiry with ID: " + id, ex);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "enquiries", allEntries = true)
    public TicketResponseDTO convertToTicket(Integer enquiryId, Integer employeeId) {
        Enquiry enquiry = repository.findById(enquiryId)
                .orElseThrow(() -> new ResourceNotFoundException("Enquiry not found with ID: " + enquiryId));

        if (Boolean.TRUE.equals(enquiry.getIsConverted())) {
            throw new BadRequestException("Enquiry has already been converted to Ticket ID: " +
                    (enquiry.getConvertedTicket() != null ? enquiry.getConvertedTicket().getTicketId() : "N/A"));
        }

        UserMaster customer = enquiry.getUser();
        if (customer == null && enquiry.getMobileNo() != null && !enquiry.getMobileNo().trim().isEmpty()) {
            Optional<UserMaster> userOpt = userMasterDao.findByMobileNo(enquiry.getMobileNo().trim());
            if (userOpt.isPresent()) {
                customer = userOpt.get();
            } else {
                customer = new UserMaster();
                String fullName = enquiry.getCustomerName() != null ? enquiry.getCustomerName().trim() : "Enquiry Customer";
                String[] nameParts = fullName.split("\\s+");
                customer.setFirstName(nameParts[0]);
                customer.setLastName(nameParts.length > 1 ? nameParts[1] : "");
                customer.setMobileNo(enquiry.getMobileNo().trim());
                customer.setEmailId(enquiry.getEmailId());
                customer.setAddress(enquiry.getAddress());
                customer.setRole("ROLE_USER");
                customer.setIsActive(true);
                customer = userMasterDao.save(customer);
            }
            enquiry.setUser(customer);
            repository.save(enquiry);
        }

        if (customer == null) {
            throw new BadRequestException("Cannot convert Enquiry to Ticket without customer details or registered user.");
        }

        // Fetch default TicketType if not specified
        Integer ticketTypeId = 1; // standard default
        List<TicketType> allTypes = ticketTypeDao.findAll();
        if (!allTypes.isEmpty()) {
            ticketTypeId = allTypes.get(0).getTicketTypeId();
        }

        // Construct TicketRequestDTO
        TicketRequestDTO ticketRequest = new TicketRequestDTO();
        ticketRequest.setUserRefNo(String.valueOf(customer.getUserId()));
        // ticketRequest.setEmailId(customer.getEmailId());
        ticketRequest.setDeviceSerialNo(enquiry.getSerialNo());
        ticketRequest.setTicketDescription(enquiry.getEnquiryFor() + ": " + enquiry.getQueryText());
        ticketRequest.setTicketTypeId(ticketTypeId);
        ticketRequest.setTicketStatusId(1); // Open
        ticketRequest.setPriority("Normal");
        ticketRequest.setEmployeeId(employeeId);
        
        if (enquiry.getBrand() != null) {
            ticketRequest.setBrandId(enquiry.getBrand().getBrandId());
        }
        if (enquiry.getDeviceModel() != null) {
            ticketRequest.setDeviceModelId(enquiry.getDeviceModel().getModelId());
        } else {
            ticketRequest.setCustomModelName(enquiry.getCustomModelName());
        }

        // Call TicketService to initialize ticket creation (runs validations, models, accessories logic)
        TicketResponseDTO ticketResponse = ticketService.create(ticketRequest);

        // Update Enquiry details
        Ticket ticket = new Ticket();
        ticket.setTicketId(ticketResponse.getTicketId());

        enquiry.setIsConverted(true);
        enquiry.setConvertedTicket(ticket);
        
        Optional<Status> resolvedStatusOpt = statusDao.getStatusByNameAndType("Replied", "ENQUIRY");
        if (resolvedStatusOpt.isEmpty()) {
            resolvedStatusOpt = statusDao.getStatusByNameAndType("Resolved", "ENQUIRY");
        }
        resolvedStatusOpt.ifPresent(enquiry::setStatus);

        repository.save(enquiry);

        return ticketResponse;
    }

    private void validateAndSetRelations(Enquiry enquiry, EnquiryRequestDTO requestDTO) {
        if (requestDTO.getUserId() != null) {
            Optional<UserMaster> userOpt = userMasterDao.findById(requestDTO.getUserId());
            if (userOpt.isPresent()) {
                enquiry.setUser(userOpt.get());
            } else {
                throw new ResourceNotFoundException("User not found with ID: " + requestDTO.getUserId());
            }
        } else if (requestDTO.getMobileNo() != null && !requestDTO.getMobileNo().trim().isEmpty()) {
            // Robust lookup or auto-creation of lightweight UserMaster record
            Optional<UserMaster> userOpt = userMasterDao.findByMobileNo(requestDTO.getMobileNo().trim());
            if (userOpt.isPresent()) {
                enquiry.setUser(userOpt.get());
            } else {
                UserMaster guest = new UserMaster();
                String fullName = requestDTO.getCustomerName() != null ? requestDTO.getCustomerName().trim() : "Guest Customer";
                String[] nameParts = fullName.split("\\s+");
                guest.setFirstName(nameParts[0]);
                guest.setLastName(nameParts.length > 1 ? nameParts[1] : "");
                guest.setMobileNo(requestDTO.getMobileNo().trim());
                guest.setEmailId(requestDTO.getEmailId() != null ? requestDTO.getEmailId().trim() : null);
                guest.setAddress(requestDTO.getAddress());
                guest.setRole("ROLE_USER");
                guest.setIsActive(true);
                UserMaster savedGuest = userMasterDao.save(guest);
                enquiry.setUser(savedGuest);
            }
        }

        if (requestDTO.getDeviceTypeId() != null) {
            Optional<DeviceType> dtOpt = deviceTypeDao.findById(requestDTO.getDeviceTypeId());
            if (dtOpt.isPresent()) {
                enquiry.setDeviceType(dtOpt.get());
            } else {
                throw new ResourceNotFoundException("Device Type not found with ID: " + requestDTO.getDeviceTypeId());
            }
        }

        if (requestDTO.getBrandId() != null) {
            Optional<Brand> brandOpt = brandDao.findById(requestDTO.getBrandId());
            if (brandOpt.isPresent()) {
                enquiry.setBrand(brandOpt.get());
            } else {
                throw new ResourceNotFoundException("Brand not found with ID: " + requestDTO.getBrandId());
            }
        }

        if (requestDTO.getModelId() != null) {
            Optional<DeviceModel> modelOpt = deviceModelDao.findById(requestDTO.getModelId());
            if (modelOpt.isPresent()) {
                enquiry.setDeviceModel(modelOpt.get());
            } else {
                throw new ResourceNotFoundException("Device Model not found with ID: " + requestDTO.getModelId());
            }
        }

        if (requestDTO.getStatusId() != null) {
            Optional<Status> statusOpt = statusDao.findById(requestDTO.getStatusId());
            if (statusOpt.isPresent()) {
                if ("ENQUIRY".equalsIgnoreCase(statusOpt.get().getStatusType())) {
                    enquiry.setStatus(statusOpt.get());
                } else {
                    throw new ResourceNotFoundException("Status has to be of type enquiry: " + requestDTO.getStatusId());
                }
            } else {
                throw new ResourceNotFoundException("Status not found with ID: " + requestDTO.getStatusId());
            }
        } else {
            Optional<Status> defaultStatusOpt = statusDao.getStatusByNameAndType("Pending", "ENQUIRY");
            if (defaultStatusOpt.isEmpty()) {
                defaultStatusOpt = statusDao.getStatusByNameAndType("Open", "ENQUIRY");
            }
            if (defaultStatusOpt.isEmpty()) {
                List<Status> enquiryStatuses = statusDao.getStatusesByType("ENQUIRY");
                if (!enquiryStatuses.isEmpty()) {
                    defaultStatusOpt = Optional.of(enquiryStatuses.get(0));
                }
            }
            if (defaultStatusOpt.isPresent()) {
                enquiry.setStatus(defaultStatusOpt.get());
            } else {
                Status newStatus = new Status();
                newStatus.setStatusName("Pending");
                newStatus.setStatusType("ENQUIRY");
                newStatus.setStatusDescription("Pending Enquiry Status");
                newStatus.setStatusFlg(1);
                Status savedStatus = statusDao.save(newStatus);
                enquiry.setStatus(savedStatus);
            }
        }
    }

    private EnquiryResponseDTO mapToResponseDTO(Enquiry enquiry) {
        EnquiryResponseDTO dto = modelMapper.map(enquiry, EnquiryResponseDTO.class);
        if (enquiry.getUser() != null) {
            dto.setUserId(enquiry.getUser().getUserId());
            dto.setUserFirstName(enquiry.getUser().getFirstName());
            dto.setUserLastName(enquiry.getUser().getLastName());
        }
        if (enquiry.getDeviceType() != null) {
            dto.setDeviceTypeId(enquiry.getDeviceType().getDeviceTypeId());
            dto.setDeviceTypeName(enquiry.getDeviceType().getDeviceTypeName());
        }
        if (enquiry.getBrand() != null) {
            dto.setBrandId(enquiry.getBrand().getBrandId());
            dto.setBrandName(enquiry.getBrand().getBrandName());
        }
        if (enquiry.getDeviceModel() != null) {
            dto.setModelId(enquiry.getDeviceModel().getModelId());
            dto.setDeviceModelName(enquiry.getDeviceModel().getModelName());
        }
        if (enquiry.getStatus() != null) {
            dto.setStatusName(enquiry.getStatus().getStatusName());
        }
        if (enquiry.getConvertedTicket() != null) {
            dto.setConvertedTicketId(enquiry.getConvertedTicket().getTicketId());
        }
        return dto;
    }
}
