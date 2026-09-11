package com.mays.srm.enquiry.service.impl;

import com.mays.srm.enquiry.dto.reqDTO.EnquiryMarkActionRequestDTO;
import com.mays.srm.enquiry.dto.reqDTO.EnquiryRequestDTO;
import com.mays.srm.enquiry.dto.resDTO.EnquiryPendingCountDTO;
import com.mays.srm.enquiry.dto.resDTO.EnquiryResponseDTO;
import com.mays.srm.enquiry.entities.Enquiry;
import com.mays.srm.enquiry.entities.UserEntryReport;
import com.mays.srm.enquiry.enums.EnquiryStatus;
import com.mays.srm.enquiry.repository.EnquiryDao;
import com.mays.srm.enquiry.repository.UserEntryReportDao;
import com.mays.srm.enquiry.service.EnquiryService;
import com.mays.srm.device.entities.Brand;
import com.mays.srm.device.entities.DeviceModel;
import com.mays.srm.device.repository.BrandDao;
import com.mays.srm.device.repository.DeviceModelDao;
import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.ticket.dto.request.TicketRequestDTO;
import com.mays.srm.ticket.dto.resDTO.TicketResponseDTO;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.ticket.entities.TicketType;
import com.mays.srm.ticket.repository.TicketTypeDao;
import com.mays.srm.ticket.service.TicketService;
import com.mays.srm.user.entities.UserMaster;
import com.mays.srm.user.repository.UserMasterDao;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class EnquiryServiceImpl implements EnquiryService {

    private static final Logger log = LoggerFactory.getLogger(EnquiryServiceImpl.class);

    private final EnquiryDao repository;
    private final UserMasterDao userMasterDao;
    private final BrandDao brandDao;
    private final DeviceModelDao deviceModelDao;
    private final UserEntryReportDao userEntryReportDao;
    private final TicketService ticketService;
    private final TicketTypeDao ticketTypeDao;
    private final ModelMapper modelMapper;

    private static final List<EnquiryStatus> COMPLETED_STATUSES = List.of(
            EnquiryStatus.TICKET_CREATED,
            EnquiryStatus.HANDED_OFF
    );

    @Autowired
    public EnquiryServiceImpl(EnquiryDao repository,
                              UserMasterDao userMasterDao,
                              BrandDao brandDao,
                              DeviceModelDao deviceModelDao,
                              UserEntryReportDao userEntryReportDao,
                              TicketService ticketService,
                              TicketTypeDao ticketTypeDao,
                              ModelMapper modelMapper) {
        this.repository = repository;
        this.userMasterDao = userMasterDao;
        this.brandDao = brandDao;
        this.deviceModelDao = deviceModelDao;
        this.userEntryReportDao = userEntryReportDao;
        this.ticketService = ticketService;
        this.ticketTypeDao = ticketTypeDao;
        this.modelMapper = modelMapper;
    }

    // ==================================================================
    // ============================ CREATE ==============================
    // ==================================================================
    @Override
    @Transactional
    @CacheEvict(value = "enquiries", allEntries = true)
    public EnquiryResponseDTO create(EnquiryRequestDTO requestDTO) {
        try {
            Enquiry enquiry = new Enquiry();
            enquiry.setIsConverted(false); // default

            applyRequestFields(enquiry, requestDTO);
            validateAndSetRelations(enquiry, requestDTO);

            Enquiry savedEnquiry = repository.save(enquiry);

            // Log only for Enquiry (0) and Inward (1). Outward on create makes no sense, but log if sent.
            if (requestDTO.getAction() == 0 || requestDTO.getAction() == 1 || requestDTO.getAction() == 2) {
                logEntry(savedEnquiry, requestDTO.getAction());
            }

            // ❌ No auto-conversion. Ticket is created ONLY via /convert-to-ticket
            return mapToResponseDTO(savedEnquiry);
        } catch (ResourceNotFoundException | DataIntegrityViolationException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Enquiry", ex);
        }
    }

    // ==================================================================
    // ============================ GET BY ID ===========================
    // ==================================================================
    @Override
    @Cacheable(value = "enquiries", key = "#id")
    public EnquiryResponseDTO getById(Integer id) {
        Enquiry enquiry = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enquiry not found with ID: " + id));
        return mapToResponseDTO(enquiry);
    }

    // ==================================================================
    // ============================ GET ALL =============================
    // ==================================================================
    @Override
    @Cacheable(value = "enquiries", key = "'all'")
    public List<EnquiryResponseDTO> getAll() {
        return new ArrayList<>(repository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList());
    }

    // ==================================================================
    // ============================ UPDATE ==============================
    // ==================================================================
    @Override
    @Transactional
    @CacheEvict(value = "enquiries", allEntries = true)
    public EnquiryResponseDTO update(Integer id, EnquiryRequestDTO requestDTO) {
        Enquiry existingEnquiry = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cannot update. Enquiry not found with ID: " + id));

        applyRequestFields(existingEnquiry, requestDTO);
        existingEnquiry.setEnquiryId(id);
        validateAndSetRelations(existingEnquiry, requestDTO);

        try {
            Enquiry updatedEnquiry = repository.save(existingEnquiry);
            // ❌ No auto-conversion on update either
            return mapToResponseDTO(updatedEnquiry);
        } catch (ResourceNotFoundException | DataIntegrityViolationException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Enquiry", ex);
        }
    }

    @Override
    @Cacheable(value = "enquiries", key = "'ticket-' + #ticketId")
    public EnquiryResponseDTO getByTicketId(Integer ticketId) {
        return repository.findByConvertedTicketTicketId(ticketId)
                .map(this::mapToResponseDTO)
                .orElse(null);
    }

    @Override
    @Transactional
    @CacheEvict(value = "enquiries", allEntries = true)
    public void markOutwardByTicket(Integer ticketId) {
        Enquiry enquiry = repository.findByConvertedTicketTicketId(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No enquiry is linked to ticket " + ticketId));

    if (enquiry.getAction() != null && enquiry.getAction() == 2) {
        throw new BadRequestException("Enquiry is already marked Outward.");
    }

        enquiry.setAction(2);
        computeAndSetStatus(enquiry);   // → HANDED_OFF
        repository.save(enquiry);

        logEntry(enquiry, 2);           // → UserEntryReport reason = "Outward"
    }

    // ==================================================================
    // ====================== GET ALL BY USER ===========================
    // ==================================================================
    @Override
    @Cacheable(value = "enquiries", key = "'user-' + #userId")
    public List<EnquiryResponseDTO> getAllEnquiriesOfUser(Integer userId) {
        return new ArrayList<>(repository.findByUserUserId(userId).stream()
                .map(this::mapToResponseDTO)
                .toList());
    }

    // ==================================================================
    // ====================== PENDING COUNT (ALL) =======================
    // ==================================================================
    @Override
    @Cacheable(value = "enquiries", key = "'pending-count-all'")
    public EnquiryPendingCountDTO getPendingCountAll() {
        long count = repository.countPendingEnquiries(COMPLETED_STATUSES);
        return new EnquiryPendingCountDTO(count);
    }

    // ==================================================================
    // ===================== PENDING COUNT (USER) =======================
    // ==================================================================
    @Override
    @Cacheable(value = "enquiries", key = "'pending-count-user-' + #userId")
    public EnquiryPendingCountDTO getPendingCountForUser(Integer userId) {
        long count = repository.countPendingEnquiriesByUser(userId, COMPLETED_STATUSES);
        return new EnquiryPendingCountDTO(count);
    }

    // ==================================================================
    // ============================ DELETE ==============================
    // ==================================================================
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

    // ==================================================================
    // ==================== CONVERT TO TICKET (MANUAL) ==================
    // Called only by employee (staff role).
    // Only allowed when: action == 1 (Inward) && !isConverted
    // ==================================================================
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

        // Enforce: only Inward (1) can be converted
        if (enquiry.getAction() == null || enquiry.getAction() != 1) {
            throw new BadRequestException(
                    "Only Inward enquiries can be converted to a ticket. Current action = " + enquiry.getAction());
        }

        return convertToTicketInternal(enquiry, employeeId);
    }

    // ==================================================================
    // ==================== MARK ACTION (USER ACTION) ===================
    // Only updates the "action" field. Does NOT create ticket.
    // Allowed: 0 = Enquiry, 1 = Inward, 2 = Outward.
    // ==================================================================
    @Override
    @Transactional
    @CacheEvict(value = "enquiries", allEntries = true)
    public void markAction(Integer enquiryId, EnquiryMarkActionRequestDTO requestDTO) {
        Enquiry enquiry = repository.findById(enquiryId)
                .orElseThrow(() -> new ResourceNotFoundException("Enquiry not found with ID: " + enquiryId));

        validateAction(requestDTO.getAction());
        enquiry.setAction(requestDTO.getAction());

        computeAndSetStatus(enquiry);

        repository.save(enquiry);
        logEntry(enquiry, requestDTO.getAction());
    }

    // ==================================================================
    // ========================= HELPER METHODS =========================
    // ==================================================================

    /**
     * Status is derived, never user-entered.
     *
     *   action = 0 (Enquiry):
     *       remark present && not converted → RESPONDED
     *       else                             → QUERIED
     *   action = 1 (Inward):
     *       isConverted = true               → TICKET_CREATED
     *       else                             → INWARDED
     *   action = 2 (Outward):                → HANDED_OFF
     */
    private void computeAndSetStatus(Enquiry enquiry) {
        int action = enquiry.getAction() != null ? enquiry.getAction() : 0;
        boolean converted = Boolean.TRUE.equals(enquiry.getIsConverted());
        boolean hasRemark = StringUtils.hasText(enquiry.getRemark());

        switch (action) {
            case 1:
                enquiry.setStatus(converted
                        ? EnquiryStatus.TICKET_CREATED
                        : EnquiryStatus.INWARDED);
                break;
            case 2:
                enquiry.setStatus(EnquiryStatus.HANDED_OFF);
                break;
            case 0:
            default:
                enquiry.setStatus((hasRemark && !converted)
                        ? EnquiryStatus.RESPONDED
                        : EnquiryStatus.QUERIED);
                break;
        }
    }

    private void applyRequestFields(Enquiry enquiry, EnquiryRequestDTO requestDTO) {
        if (requestDTO.getEnquiryFor() != null) {
            enquiry.setEnquiryFor(requestDTO.getEnquiryFor());
        }
        if (requestDTO.getQueryText() != null) {
            enquiry.setQueryText(requestDTO.getQueryText());
        }

        // ── Remark is employee-only.
        //   CREATE (enquiryId == null) → ignore, whatever the client sends.
        //   UPDATE (enquiryId != null) → accept, it's a staff response.
        if (enquiry.getEnquiryId() != null && requestDTO.getRemark() != null) {
            enquiry.setRemark(requestDTO.getRemark());
        }

        // Serial No mandatory on create
        if (StringUtils.hasText(requestDTO.getSerialNo())) {
            enquiry.setSerialNo(requestDTO.getSerialNo().trim());
        } else if (enquiry.getEnquiryId() == null) {
            throw new BadRequestException("serialNo is required");
        }

        // Action first, then status
        validateAction(requestDTO.getAction());
        enquiry.setAction(requestDTO.getAction());

        computeAndSetStatus(enquiry);
    }

    private void validateAndSetRelations(Enquiry enquiry, EnquiryRequestDTO requestDTO) {
        // User
        if (requestDTO.getUserId() != null) {
            UserMaster user = userMasterDao.findById(requestDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found with ID: " + requestDTO.getUserId()));
            enquiry.setUser(user);
        }

        // Device Model
        if (requestDTO.getModelId() != null) {
            DeviceModel model = deviceModelDao.findById(requestDTO.getModelId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Device Model not found with ID: " + requestDTO.getModelId()));
            enquiry.setDeviceModel(model);
        } else if (StringUtils.hasText(requestDTO.getCustomModelName())) {
            if (requestDTO.getBrandId() == null) {
                throw new BadRequestException("brandId is required to create a custom model");
            }

            String customName = requestDTO.getCustomModelName().trim();
            DeviceModel resolvedModel = deviceModelDao
                    .findByModelNameIgnoreCaseAndBrandBrandId(customName, requestDTO.getBrandId())
                    .orElseGet(() -> {
                        Brand brand = brandDao.findById(requestDTO.getBrandId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "Brand not found with ID: " + requestDTO.getBrandId()));

                        DeviceModel newModel = new DeviceModel();
                        newModel.setModelName(customName);
                        newModel.setBrand(brand);
                        newModel.setModelDescription("Custom created from enquiry");
                        return deviceModelDao.save(newModel);
                    });

            enquiry.setDeviceModel(resolvedModel);
        }

        // If action is Inward, customer/user is required
        if (enquiry.getAction() != null && enquiry.getAction() == 1 && enquiry.getUser() == null) {
            throw new BadRequestException("User/customer is required when action is Inward (1)");
        }
    }

    private void validateAction(Integer action) {
        if (action == null || action < 0 || action > 2) {
            throw new BadRequestException(
                    "Invalid action. Allowed values: 0 = Enquiry, 1 = Inward, 2 = Outward");
        }
    }

    // ------------------------------------------------------------------
    // Ticket creation — the ONLY path that creates a ticket from an enquiry
    // ------------------------------------------------------------------
    private TicketResponseDTO convertToTicketInternal(Enquiry enquiry, Integer employeeId) {
        if (Boolean.TRUE.equals(enquiry.getIsConverted())) {
            throw new BadRequestException("Enquiry has already been converted to Ticket ID: " +
                    (enquiry.getConvertedTicket() != null
                            ? enquiry.getConvertedTicket().getTicketId() : "N/A"));
        }

        UserMaster customer = enquiry.getUser();
        if (customer == null) {
            throw new BadRequestException(
                    "Cannot convert Enquiry to Ticket without customer/user.");
        }

        // Guard: serial number is required by TicketDeviceService
        if (!StringUtils.hasText(enquiry.getSerialNo())) {
            throw new BadRequestException(
                    "Cannot convert to ticket: enquiry has no serial number. " +
                            "Update the enquiry with a valid serial number first.");
        }

        TicketRequestDTO ticketRequest = new TicketRequestDTO();
        ticketRequest.setUserRefNo(String.valueOf(customer.getUserId()));
        ticketRequest.setDeviceSerialNo(enquiry.getSerialNo().trim());
        ticketRequest.setTicketDescription(buildTicketDescription(enquiry));
        ticketRequest.setTicketTypeId(resolveDefaultTicketTypeId());
        ticketRequest.setTicketStatusId(1);  // Open
        ticketRequest.setPriority("Normal");
        ticketRequest.setEmployeeId(employeeId);

        if (enquiry.getDeviceModel() != null) {
            ticketRequest.setDeviceModelId(enquiry.getDeviceModel().getModelId());
            if (enquiry.getDeviceModel().getBrand() != null) {
                ticketRequest.setBrandId(enquiry.getDeviceModel().getBrand().getBrandId());
            }
        }

        // ── Log full payload so a failing create is easy to debug
        log.info("[Enquiry→Ticket] Request payload for enquiry {}: {}", enquiry.getEnquiryId(), ticketRequest);

        TicketResponseDTO ticketResponse;
        try {
            ticketResponse = ticketService.create(ticketRequest);
        } catch (Exception ex) {
            log.error("[Enquiry→Ticket] ticketService.create failed for enquiry {}: {}",
                    enquiry.getEnquiryId(), ex.getMessage(), ex);
            throw ex;   // re-throw so @Transactional rolls back and API surfaces cause
        }

        log.info("[Enquiry→Ticket] Ticket created. enquiryId={}, ticketId={}",
                enquiry.getEnquiryId(), ticketResponse.getTicketId());

        Ticket ticket = new Ticket();
        ticket.setTicketId(ticketResponse.getTicketId());

        enquiry.setIsConverted(true);
        enquiry.setConvertedTicket(ticket);
        enquiry.setAction(1); // ensure inward
        computeAndSetStatus(enquiry);   // → TICKET_CREATED

        repository.save(enquiry);
        logEntry(enquiry, 1); // Inward

        return ticketResponse;
    }

    private Integer resolveDefaultTicketTypeId() {
        List<TicketType> allTypes = ticketTypeDao.findAll();
        if (!allTypes.isEmpty()) {
            return allTypes.get(0).getTicketTypeId();
        }
        return 1; // fallback
    }

    private String buildTicketDescription(Enquiry enquiry) {
        String enquiryFor = enquiry.getEnquiryFor() != null ? enquiry.getEnquiryFor() : "Enquiry";
        String queryText = enquiry.getQueryText() != null ? enquiry.getQueryText() : "";
        return enquiryFor + ": " + queryText;
    }

    /**
     * UserEntryReport reason mapping.
     *   0 → "Enquiry"
     *   1 → "Inward"
     *   2 → "Outward"
     * User is taken from enquiry.getUser() — no SecurityUtils lookup.
     */
    private void logEntry(Enquiry enquiry, int action) {
        try {
            UserMaster user = enquiry.getUser();
            if (user == null) {
                return;
            }

            String reason;
            switch (action) {
                case 1:
                    reason = "Inward";
                    break;
                case 2:
                    reason = "Outward";
                    break;
                case 0:
                default:
                    reason = "Enquiry";
                    break;
            }

            UserEntryReport report = new UserEntryReport();
            report.setUser(user);
            report.setReason(reason);
            report.setEnquiry(enquiry);
            userEntryReportDao.save(report);
        } catch (Exception e) {
            log.warn("Failed to write UserEntryReport for enquiry {}: {}",
                    enquiry.getEnquiryId(), e.getMessage());
        }
    }

    private EnquiryResponseDTO mapToResponseDTO(Enquiry enquiry) {
        EnquiryResponseDTO dto = modelMapper.map(enquiry, EnquiryResponseDTO.class);

        if (enquiry.getUser() != null) {
            dto.setUserId(enquiry.getUser().getUserId());
            dto.setUserFirstName(enquiry.getUser().getFirstName());
            dto.setUserLastName(enquiry.getUser().getLastName());
            dto.setMobileNo(enquiry.getUser().getMobileNo());
            dto.setEmailId(enquiry.getUser().getEmailId());
        }

        if (enquiry.getDeviceModel() != null) {
            DeviceModel model = enquiry.getDeviceModel();
            dto.setModelId(model.getModelId());
            dto.setDeviceModelName(model.getModelName());

            if (model.getBrand() != null) {
                Brand brand = model.getBrand();
                dto.setBrandId(brand.getBrandId());
                dto.setBrandName(brand.getBrandName());

                if (brand.getDeviceType() != null) {
                    dto.setDeviceTypeId(brand.getDeviceType().getDeviceTypeId());
                    dto.setDeviceTypeName(brand.getDeviceType().getDeviceTypeName());
                }
            }
        }

        if (enquiry.getStatus() != null) {
            dto.setStatus(enquiry.getStatus().name());
        }

        if (enquiry.getConvertedTicket() != null) {
            dto.setConvertedTicketId(enquiry.getConvertedTicket().getTicketId());
        }

        dto.setIsConverted(enquiry.getIsConverted());
        dto.setAction(enquiry.getAction());
        dto.setInsertDate(enquiry.getInsertDate());

        return dto;
    }
}