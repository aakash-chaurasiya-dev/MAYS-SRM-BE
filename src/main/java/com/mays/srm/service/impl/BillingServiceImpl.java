package com.mays.srm.service.impl;

import com.mays.srm.dao.core.*;
import com.mays.srm.dto.requestDTO.BillingRequestDTO;
import com.mays.srm.dto.responseDTO.BillingResponseDTO;
import com.mays.srm.entity.*;
import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.BillingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BillingServiceImpl implements BillingService {

    private final BillingDao repository;
    private final TicketDao ticketDao;
    private final ChargeTypeDao chargeTypeDao;
    private final InventoryDao inventoryDao;
    private final ServiceChargesDao serviceChargesDao;
    private final PaymentModeDetailsDao paymentModeDetailsDao;
    private final StatusDao statusDao;
    private final ModelMapper modelMapper;

    @Autowired
    public BillingServiceImpl(BillingDao repository, TicketDao ticketDao, ChargeTypeDao chargeTypeDao, InventoryDao inventoryDao, ServiceChargesDao serviceChargesDao, PaymentModeDetailsDao paymentModeDetailsDao, StatusDao statusDao, ModelMapper modelMapper) {
        this.repository = repository;
        this.ticketDao = ticketDao;
        this.chargeTypeDao = chargeTypeDao;
        this.inventoryDao = inventoryDao;
        this.serviceChargesDao = serviceChargesDao;
        this.paymentModeDetailsDao = paymentModeDetailsDao;
        this.statusDao = statusDao;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public BillingResponseDTO create(BillingRequestDTO requestDTO) {
        try {
            Billing billing = new Billing();
            
            // Set Billing Date
            billing.setBillingDate(LocalDateTime.now());

            // Validate and set relations
            validateAndSetRelations(billing, requestDTO);

            // Business logic for amount calculation
            calculateAmount(billing, requestDTO);

            Billing savedBilling = repository.save(billing);
            return mapToResponseDTO(savedBilling);
        } catch (ResourceNotFoundException | DataIntegrityViolationException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Billing record", ex);
        }
    }

    @Override
    public BillingResponseDTO getById(Integer id) {
        Optional<Billing> billingOpt = repository.findById(id);
        if (billingOpt.isPresent()) {
            return mapToResponseDTO(billingOpt.get());
        } else {
            throw new ResourceNotFoundException("Billing record not found with ID: " + id);
        }
    }

    @Override
    public List<BillingResponseDTO> getAll() {
        List<Billing> billingList = repository.findAll();
        List<BillingResponseDTO> dtoList = new ArrayList<>();
        for (Billing billing : billingList) {
            dtoList.add(mapToResponseDTO(billing));
        }
        return dtoList;
    }

    @Override
    @Transactional
    public BillingResponseDTO update(Integer id, BillingRequestDTO requestDTO) {
        Optional<Billing> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Billing record not found with ID: " + id);
        }
        
        Billing existingBilling = existingOpt.get();
        
        validateAndSetRelations(existingBilling, requestDTO);
        calculateAmount(existingBilling, requestDTO);
        existingBilling.setBillingId(id);
        try {
            Billing updatedBilling = repository.save(existingBilling);
            return mapToResponseDTO(updatedBilling);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Billing record", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Billing record not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Billing record with ID: " + id, ex);
        }
    }

    private void validateAndSetRelations(Billing billing, BillingRequestDTO requestDTO) {
        // Ticket
        if (requestDTO.getTicketId() != null) {
            Optional<Ticket> ticketOpt = ticketDao.findById(requestDTO.getTicketId());
            if (ticketOpt.isPresent()) {
                billing.setTicket(ticketOpt.get());
            } else {
                throw new ResourceNotFoundException("Ticket not found with ID: " + requestDTO.getTicketId());
            }
        }

        // Charge Type
        if (requestDTO.getChargeTypeId() != null) {
            Optional<ChargeType> chargeTypeOpt = chargeTypeDao.findById(requestDTO.getChargeTypeId());
            if (chargeTypeOpt.isPresent()) {
                billing.setChargeType(chargeTypeOpt.get());
            } else {
                throw new ResourceNotFoundException("Charge Type not found with ID: " + requestDTO.getChargeTypeId());
            }
        }

        // Payment Mode
        if (requestDTO.getPaymentModeId() != null) {
            Optional<PaymentModeDetails> paymentModeOpt = paymentModeDetailsDao.findById(requestDTO.getPaymentModeId());
            if (paymentModeOpt.isPresent()) {
                billing.setPaymentModeDetails(paymentModeOpt.get());
            } else {
                throw new ResourceNotFoundException("Payment Mode not found with ID: " + requestDTO.getPaymentModeId());
            }
        }

        // Status
        if (requestDTO.getStatusId() != null) {
            Optional<Status> statusOpt = statusDao.findById(requestDTO.getStatusId());
            if (statusOpt.isPresent()) {
                billing.setStatus(statusOpt.get());
            } else {
                throw new ResourceNotFoundException("Status not found with ID: " + requestDTO.getStatusId());
            }
        }
    }

    private void calculateAmount(Billing billing, BillingRequestDTO requestDTO) {
        if (billing.getChargeType() != null) {
            String chargeName = billing.getChargeType().getChargeName();

            if ("Product Charge".equalsIgnoreCase(chargeName)) {
                if (requestDTO.getProductId() == null) {
                    throw new BadRequestException("Product ID is required for a 'Product Charge'.");
                }
                Optional<Inventory> productOpt = inventoryDao.findById(requestDTO.getProductId());
                if (productOpt.isPresent()) {
                    billing.setProduct(productOpt.get());
                    billing.setAmount(productOpt.get().getSellingPrice());
                    billing.setServiceCharge(null);
                } else {
                    throw new ResourceNotFoundException("Product not found with ID: " + requestDTO.getProductId());
                }
            } else if ("Service Charge".equalsIgnoreCase(chargeName)) {
                if (requestDTO.getServiceChargeId() == null) {
                    throw new BadRequestException("Service Charge ID is required for a 'Service Charge'.");
                }
                Optional<ServiceCharges> serviceChargeOpt = serviceChargesDao.findById(requestDTO.getServiceChargeId());
                if (serviceChargeOpt.isPresent()) {
                    billing.setServiceCharge(serviceChargeOpt.get());
                    billing.setAmount(serviceChargeOpt.get().getAmount());
                    billing.setProduct(null);
                } else {
                    throw new ResourceNotFoundException("Service Charge not found with ID: " + requestDTO.getServiceChargeId());
                }
            } else {
                if (requestDTO.getAmount() == null || requestDTO.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                    throw new BadRequestException("A valid amount is required for this charge type.");
                }
                billing.setAmount(requestDTO.getAmount());
                billing.setProduct(null);
                billing.setServiceCharge(null);
            }
        }
    }

    private BillingResponseDTO mapToResponseDTO(Billing billing) {
        BillingResponseDTO dto = modelMapper.map(billing, BillingResponseDTO.class);
        
        if (billing.getTicket() != null) {
            dto.setTicketId(billing.getTicket().getTicketId());
        }
        if (billing.getChargeType() != null) {
            dto.setChargeTypeName(billing.getChargeType().getChargeName());
        }
        if (billing.getProduct() != null) {
            dto.setProductName(billing.getProduct().getProductName());
        }
        if (billing.getServiceCharge() != null) {
            dto.setServiceChargeDescription(billing.getServiceCharge().getDescr());
        }
        if (billing.getPaymentModeDetails() != null) {
            dto.setPaymentModeName(billing.getPaymentModeDetails().getPaymentMode());
        }
        if (billing.getStatus() != null) {
            dto.setStatusName(billing.getStatus().getStatusName());
        }
        
        return dto;
    }
}
