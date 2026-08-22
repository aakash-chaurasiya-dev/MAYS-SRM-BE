package com.mays.srm.inventory.service.impl;

import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.inventory.dto.request.TicketPartApproveRequestDTO;
import com.mays.srm.inventory.dto.request.TicketPartRequestDTO;
import com.mays.srm.inventory.dto.resDTO.TicketPartResponseDTO;
import com.mays.srm.inventory.entities.ProductList;
import com.mays.srm.inventory.entities.TicketPart;
import com.mays.srm.inventory.enums.TicketPartStatus;
import com.mays.srm.inventory.repository.ProductListDao;
import com.mays.srm.inventory.repository.TicketPartDao;
import com.mays.srm.inventory.service.TicketPartService;
import com.mays.srm.inventory.util.InventoryAuditHelper;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.ticket.repository.TicketDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketPartServiceImpl implements TicketPartService {

    private final TicketPartDao ticketPartDao;
    private final TicketDao ticketDao;
    private final ProductListDao productListDao;

    public TicketPartServiceImpl(TicketPartDao ticketPartDao, TicketDao ticketDao, ProductListDao productListDao) {
        this.ticketPartDao = ticketPartDao;
        this.ticketDao = ticketDao;
        this.productListDao = productListDao;
    }

    @Override
    public List<TicketPartResponseDTO> getByTicketId(Integer ticketId) {
        return ticketPartDao.findDetailsByTicketId(ticketId);
    }

    @Override
    public List<TicketPartResponseDTO> getAll() {
        return ticketPartDao.findAllDetails();
    }

    @Override
    @Transactional
    public TicketPartResponseDTO create(TicketPartRequestDTO request) {
        Ticket ticket = ticketDao.findById(request.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + request.getTicketId()));
        ProductList product = productListDao.findById(request.getPartCatId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.getPartCatId()));

        TicketPart entity = new TicketPart();
        entity.setTicket(ticket);
        entity.setProductList(product);
        entity.setQuantity(request.getQuantity() != null && request.getQuantity() > 0 ? request.getQuantity() : 1);
        entity.setRemark(request.getRemark());
        entity.setPartStatus(TicketPartStatus.REQUESTED);
        entity.setCreatedBy(InventoryAuditHelper.currentEmployeeId());
        entity.setUpdatedBy(InventoryAuditHelper.currentEmployeeId());
        ticketPartDao.save(entity);

        return ticketPartDao.findDetailsByTicketId(ticket.getTicketId()).stream()
                .filter(r -> r.getTicketPartId().equals(entity.getTicketPartId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Ticket part not found after create"));
    }

    @Override
    @Transactional
    public TicketPartResponseDTO update(Integer ticketPartId, TicketPartRequestDTO request) {
        TicketPart entity = ticketPartDao.findById(ticketPartId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket part not found: " + ticketPartId));

        if (entity.getPartStatus() != TicketPartStatus.REQUESTED
                && entity.getPartStatus() != TicketPartStatus.APPROVED) {
            throw new BadRequestException("Cannot update part in status: " + entity.getPartStatus());
        }

        if (request.getPartCatId() != null && !request.getPartCatId().equals(entity.getProductList().getPartCatId())) {
            ProductList product = productListDao.findById(request.getPartCatId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.getPartCatId()));
            entity.setProductList(product);
        }
        if (request.getQuantity() != null && request.getQuantity() > 0) {
            entity.setQuantity(request.getQuantity());
        }
        if (request.getRemark() != null) {
            entity.setRemark(request.getRemark());
        }
        entity.setUpdatedBy(InventoryAuditHelper.currentEmployeeId());
        ticketPartDao.save(entity);

        Integer ticketId = entity.getTicket().getTicketId();
        return ticketPartDao.findDetailsByTicketId(ticketId).stream()
                .filter(r -> r.getTicketPartId().equals(ticketPartId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Ticket part not found after update"));
    }

    @Override
    @Transactional
    public TicketPartResponseDTO approve(Integer ticketPartId, TicketPartApproveRequestDTO request) {
        TicketPart entity = ticketPartDao.findById(ticketPartId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket part not found: " + ticketPartId));

        boolean approved = Boolean.TRUE.equals(request.getApproved());
        entity.setManagerApproval(approved);
        entity.setManagerApprovedAt(LocalDateTime.now());
        entity.setPartStatus(approved ? TicketPartStatus.APPROVED : TicketPartStatus.REJECTED);
        entity.setUpdatedBy(InventoryAuditHelper.currentEmployeeId());
        ticketPartDao.save(entity);

        Integer ticketId = entity.getTicket().getTicketId();
        return ticketPartDao.findDetailsByTicketId(ticketId).stream()
                .filter(r -> r.getTicketPartId().equals(ticketPartId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Ticket part not found after approve"));
    }

    @Override
    @Transactional
    public TicketPartResponseDTO customerApprove(Integer ticketPartId, TicketPartApproveRequestDTO request) {
        TicketPart entity = ticketPartDao.findById(ticketPartId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket part not found: " + ticketPartId));

        if (!Boolean.TRUE.equals(entity.getManagerApproval())) {
            throw new BadRequestException("Manager must approve before customer approval");
        }
        if (entity.getPartStatus() == TicketPartStatus.ORDERED
                || entity.getPartStatus() == TicketPartStatus.PARTIAL
                || entity.getPartStatus() == TicketPartStatus.RECEIVED
                || entity.getPartStatus() == TicketPartStatus.REJECTED
                || entity.getPartStatus() == TicketPartStatus.CANCELLED) {
            throw new BadRequestException("Cannot change customer approval in status: " + entity.getPartStatus());
        }

        boolean approved = Boolean.TRUE.equals(request.getApproved());
        entity.setCustomerApproval(approved);
        entity.setCustomerApprovedAt(approved ? LocalDateTime.now() : null);
        entity.setUpdatedBy(InventoryAuditHelper.currentEmployeeId());
        ticketPartDao.save(entity);

        Integer ticketId = entity.getTicket().getTicketId();
        return ticketPartDao.findDetailsByTicketId(ticketId).stream()
                .filter(r -> r.getTicketPartId().equals(ticketPartId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Ticket part not found after customer approve"));
    }

    @Override
    @Transactional
    public void delete(Integer ticketPartId) {
        TicketPart entity = ticketPartDao.findById(ticketPartId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket part not found: " + ticketPartId));

        if (entity.getPartStatus() == TicketPartStatus.ORDERED
                || entity.getPartStatus() == TicketPartStatus.PARTIAL
                || entity.getPartStatus() == TicketPartStatus.RECEIVED) {
            throw new BadRequestException("Cannot delete part already in order flow");
        }

        entity.setPartStatus(TicketPartStatus.CANCELLED);
        entity.setUpdatedBy(InventoryAuditHelper.currentEmployeeId());
        ticketPartDao.save(entity);
    }
}
