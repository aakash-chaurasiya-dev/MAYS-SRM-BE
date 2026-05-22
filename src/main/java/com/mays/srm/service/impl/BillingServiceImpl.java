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

    @Autowired
    public BillingServiceImpl(
            BillingDao billingDao,
            InventoryDao inventoryDao,
            ServiceChargesDao serviceChargesDao,
            TicketDao ticketDao,
            ChargeTypeDao chargeTypeDao
    ) {
        this.billingDao = billingDao;
        this.inventoryDao = inventoryDao;
        this.serviceChargesDao = serviceChargesDao;
        this.ticketDao = ticketDao;
        this.chargeTypeDao = chargeTypeDao;
    }

    @Override
    @Transactional
    public Billing create(Billing billing) {
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
    public Billing update(Billing entity) {
        // Similar logic as create can be implemented here if needed
        return billingDao.save(entity);
    }

    @Override
    public void delete(Integer id) {
        billingDao.deleteById(id);
    }
}
