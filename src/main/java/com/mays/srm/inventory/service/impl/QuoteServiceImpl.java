package com.mays.srm.inventory.service.impl;

import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.inventory.dto.request.QuoteRequestDTO;
import com.mays.srm.inventory.dto.resDTO.QuoteResponseDTO;
import com.mays.srm.inventory.entities.ProductList;
import com.mays.srm.inventory.entities.Quote;
import com.mays.srm.inventory.entities.TicketPart;
import com.mays.srm.inventory.enums.QuoteStatus;
import com.mays.srm.inventory.enums.TicketPartStatus;
import com.mays.srm.inventory.repository.ProductListDao;
import com.mays.srm.inventory.repository.QuoteDao;
import com.mays.srm.inventory.repository.TicketPartDao;
import com.mays.srm.inventory.service.QuoteService;
import com.mays.srm.inventory.util.InventoryAuditHelper;
import com.mays.srm.notification.service.NotificationService;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.ticket.repository.TicketDao;
import com.mays.srm.ticket.service.impl.TicketQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuoteServiceImpl implements QuoteService {

    private final QuoteDao quoteDao;
    private final TicketPartDao ticketPartDao;
    private final TicketDao ticketDao;
    private final ProductListDao productListDao;
    private final NotificationService notificationService;

    @Autowired
    public QuoteServiceImpl(
            QuoteDao quoteDao,
            TicketPartDao ticketPartDao,
            TicketDao ticketDao,
            ProductListDao productListDao,
            NotificationService notificationService) {
        this.quoteDao = quoteDao;
        this.ticketPartDao = ticketPartDao;
        this.ticketDao = ticketDao;
        this.productListDao = productListDao;
        this.notificationService = notificationService;
    }

    @Override
    public List<QuoteResponseDTO> getByTicketPartId(Integer ticketPartId) {
        return quoteDao.findDetailsByTicketPartId(ticketPartId);
    }

    @Override
    public List<QuoteResponseDTO> getByTicketId(Integer ticketId) {
        return quoteDao.findDetailsByTicketId(ticketId);
    }

    @Override
    @Transactional
    public QuoteResponseDTO create(QuoteRequestDTO request) {
        TicketPart ticketPart = requireApprovedTicketPart(request.getTicketPartId());
        Ticket ticket = resolveTicket(request.getTicketId(), ticketPart);
        ProductList product = resolveProduct(request.getPartCatId(), ticketPart);

        Quote quote = new Quote();
        quote.setTicket(ticket);
        quote.setTicketPart(ticketPart);
        quote.setProductList(product);
        quote.setSalesPrice(request.getSalesPrice());
        quote.setDescription(request.getDescription());
        quote.setSubject(request.getSubject());
        quote.setBody(request.getBody());
        quote.setValidUntil(request.getValidUntil());
        quote.setStatus(QuoteStatus.DRAFT);
        quote.setCreatedBy(InventoryAuditHelper.currentEmployeeId());
        quote.setUpdatedBy(InventoryAuditHelper.currentEmployeeId());
        quoteDao.save(quote);

        // Enqueue notification email
        if (quote.getTicket().getUserMaster().getEmailId() != null && !quote.getTicket().getUserMaster().getEmailId().isEmpty()) {
            java.util.Map<String, Object> variables = new java.util.HashMap<>();
            variables.put("ticketNo", quote.getTicket().getTicketId());
            variables.put("Part_name", quote.getProductList().getPartName());
            variables.put("Price", quote.getSalesPrice());
            variables.put("Description",quote.getDescription());
            variables.put("body",quote.getBody());
            variables.put("company_name", "Mays Computer Repair & Solutions");
            notificationService.enqueueEmail(quote.getTicket().getUserMaster().getEmailId(), quote.getSubject() + quote.getTicket().getTicketId(), "send_quotes", variables);
        }

        return findQuoteResponse(quote.getQuoteId(), ticketPart.getTicketPartId());
    }

    @Override
    @Transactional
    public QuoteResponseDTO update(Integer quoteId, QuoteRequestDTO request) {
        Quote quote = quoteDao.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found: " + quoteId));

        if (quote.getStatus() != QuoteStatus.DRAFT) {
            throw new BadRequestException("Only draft quotes can be updated");
        }

        if (request.getSalesPrice() != null) {
            quote.setSalesPrice(request.getSalesPrice());
        }
        if (request.getDescription() != null) {
            quote.setDescription(request.getDescription());
        }
        if (request.getSubject() != null) {
            quote.setSubject(request.getSubject());
        }
        if (request.getBody() != null) {
            quote.setBody(request.getBody());
        }
        if (request.getValidUntil() != null) {
            quote.setValidUntil(request.getValidUntil());
        }
        quote.setUpdatedBy(InventoryAuditHelper.currentEmployeeId());
        quoteDao.save(quote);

        return findQuoteResponse(quoteId, quote.getTicketPart().getTicketPartId());
    }

    @Override
    @Transactional
    public QuoteResponseDTO send(Integer quoteId) {
        Quote quote = quoteDao.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found: " + quoteId));

        TicketPart ticketPart = quote.getTicketPart();
        requireApprovedTicketPart(ticketPart.getTicketPartId());

        quote.setStatus(QuoteStatus.SENT);
        quote.setSentAt(LocalDateTime.now());
        quote.setSentBy(InventoryAuditHelper.currentEmployeeId());
        quote.setUpdatedBy(InventoryAuditHelper.currentEmployeeId());
        quoteDao.save(quote);

        ticketPart.setPartStatus(TicketPartStatus.QUOTED);
        ticketPart.setSendQuotes(true);
        ticketPart.setQuotesSentAt(LocalDateTime.now());
        ticketPart.setUpdatedBy(InventoryAuditHelper.currentEmployeeId());
        ticketPartDao.save(ticketPart);

        return findQuoteResponse(quoteId, ticketPart.getTicketPartId());
    }

    private TicketPart requireApprovedTicketPart(Integer ticketPartId) {
        TicketPart ticketPart = ticketPartDao.findById(ticketPartId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket part not found: " + ticketPartId));
        if (!Boolean.TRUE.equals(ticketPart.getManagerApproval())) {
            throw new BadRequestException("Ticket part must be approved before quote actions");
        }
        return ticketPart;
    }

    private Ticket resolveTicket(Integer ticketId, TicketPart ticketPart) {
        if (ticketId != null) {
            return ticketDao.findById(ticketId)
                    .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));
        }
        return ticketPart.getTicket();
    }

    private ProductList resolveProduct(Integer partCatId, TicketPart ticketPart) {
        if (partCatId != null) {
            return productListDao.findById(partCatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + partCatId));
        }
        return ticketPart.getProductList();
    }

    private QuoteResponseDTO findQuoteResponse(Integer quoteId, Integer ticketPartId) {
        return quoteDao.findDetailsByTicketPartId(ticketPartId).stream()
                .filter(q -> q.getQuoteId().equals(quoteId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found after save"));
    }
}
