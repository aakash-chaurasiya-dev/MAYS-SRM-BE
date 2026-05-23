package com.mays.srm.service.impl;

import com.mays.srm.dao.core.*;
import com.mays.srm.entity.*;
import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BillingServiceImpl implements BillingService {

    private final BillingDao billingDao;
    private final InventoryDao inventoryDao;
    private final ServiceChargesDao serviceChargesDao;
    private final TicketDao ticketDao;
    private final ChargeTypeDao chargeTypeDao;
    private final StatusDao statusDao;
    private final PaymentModeDetailsDao paymentModeDetailsDao;

    @Autowired
    public BillingServiceImpl(
            BillingDao billingDao,
            InventoryDao inventoryDao,
            ServiceChargesDao serviceChargesDao,
            TicketDao ticketDao,
            ChargeTypeDao chargeTypeDao,
            StatusDao statusDao,
            PaymentModeDetailsDao paymentModeDetailsDao) {
        this.billingDao = billingDao;
        this.inventoryDao = inventoryDao;
        this.serviceChargesDao = serviceChargesDao;
        this.ticketDao = ticketDao;
        this.chargeTypeDao = chargeTypeDao;
        this.statusDao = statusDao;
        this.paymentModeDetailsDao = paymentModeDetailsDao;
    }

    @Override
    @Transactional
    public Billing create(Billing billing) {
        validateStatus(billing.getStatus());
        validatePaymentMode(billing.getPaymentModeDetails());

        // 1. Validate and fetch the ChargeType
        Optional<ChargeType> chargeTypeOpt = chargeTypeDao.findById(billing.getChargeType().getChargeTypeId());
        if (!chargeTypeOpt.isPresent()) {
            throw new ResourceNotFoundException("ChargeType not found with ID: " + billing.getChargeType().getChargeTypeId());
        }
        ChargeType chargeType = chargeTypeOpt.get();
        billing.setChargeType(chargeType);

        // 2. Validate and fetch the Ticket
        Optional<Ticket> ticketOpt = ticketDao.findById(billing.getTicket().getTicketId());
        if (!ticketOpt.isPresent()) {
            throw new ResourceNotFoundException("Ticket not found with ID: " + billing.getTicket().getTicketId());
        }
        Ticket ticket = ticketOpt.get();
        billing.setTicket(ticket);

        // 3. Set amount based on ChargeType
        String chargeName = chargeType.getChargeName();

        if ("Product Charge".equalsIgnoreCase(chargeName)) {
            if (billing.getProduct() == null || billing.getProduct().getProductId() == null) {
                throw new BadRequestException("Product ID is required for a 'Product Charge'.");
            }
            
            Optional<Inventory> productOpt = inventoryDao.findById(billing.getProduct().getProductId());
            if (!productOpt.isPresent()) {
                throw new ResourceNotFoundException("Product not found with ID: " + billing.getProduct().getProductId());
            }
            
            Inventory product = productOpt.get();
            billing.setProduct(product);
            billing.setAmount(product.getSellingPrice());
            billing.setServiceCharge(null); // Ensure service charge is null

        } else if ("Service Charge".equalsIgnoreCase(chargeName)) {
            if (billing.getServiceCharge() == null || billing.getServiceCharge().getChargeId() == null) {
                throw new BadRequestException("Service Charge ID is required for a 'Service Charge'.");
            }

            Optional<ServiceCharges> serviceChargeOpt = serviceChargesDao.findById(billing.getServiceCharge().getChargeId());
            if (!serviceChargeOpt.isPresent()) {
                throw new ResourceNotFoundException("Service Charge not found with ID: " + billing.getServiceCharge().getChargeId());
            }

            ServiceCharges serviceCharge = serviceChargeOpt.get();
            billing.setServiceCharge(serviceCharge);
            billing.setAmount(serviceCharge.getAmount());
            billing.setProduct(null); // Ensure product is null

        } else {
            // For any other charge type, the amount is provided by the user
            if (billing.getAmount() == null || billing.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new BadRequestException("A valid amount is required for this charge type.");
            }
            billing.setProduct(null);
            billing.setServiceCharge(null);
        }

        return billingDao.save(billing);
    }

    @Override
    public Optional<Billing> getById(Integer id) {
        return billingDao.findById(id);
    }

    @Override
    public List<Billing> getAll() {
        return billingDao.findAll();
    }

    @Override
    @Transactional
    public Billing update(Billing entity) {
        if (entity.getBillingId() == null || !billingDao.existsById(entity.getBillingId())) {
            throw new ResourceNotFoundException("Billing with ID " + entity.getBillingId() + " not found for update.");
        }
        validateStatus(entity.getStatus());
        validatePaymentMode(entity.getPaymentModeDetails());
        return billingDao.save(entity);
    }

    @Override
    public void delete(Integer id) {
        billingDao.deleteById(id);

    }

    private void validateStatus(Status status) {
        if (status != null && status.getStatusId() != null) {
            Status dbStatus = statusDao.findById(status.getStatusId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid status ID"));
            if (!"billing".equalsIgnoreCase(dbStatus.getStatusType())) {
                throw new IllegalArgumentException("Status must be of type 'billing'");
            }
        }
    }

    private void validatePaymentMode(PaymentModeDetails paymentModeDetails) {
        if (paymentModeDetails != null && paymentModeDetails.getPayModeId() != null) {
            paymentModeDetailsDao.findById(paymentModeDetails.getPayModeId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Payment Mode ID"));
        }
    }
}