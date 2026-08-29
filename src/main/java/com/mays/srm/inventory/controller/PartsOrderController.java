package com.mays.srm.inventory.controller;

import com.mays.srm.inventory.dto.request.PartsOrderOpenRequestDTO;
import com.mays.srm.inventory.dto.request.PartsOrderSaveRequestDTO;
import com.mays.srm.inventory.dto.request.PartsOrderStatusRequestDTO;
import com.mays.srm.inventory.dto.resDTO.PartsOrderModalResponseDTO;
import com.mays.srm.inventory.dto.resDTO.PartsOrderSummaryResponseDTO;
import com.mays.srm.inventory.service.PartsOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parts-orders")
public class PartsOrderController {

    private final PartsOrderService partsOrderService;

    public PartsOrderController(PartsOrderService partsOrderService) {
        this.partsOrderService = partsOrderService;
    }

    @GetMapping
    public ResponseEntity<List<PartsOrderSummaryResponseDTO>> getAll() {
        return ResponseEntity.ok(partsOrderService.getAll());
    }

    @GetMapping("/ticket-part/{ticketPartId}")
    public ResponseEntity<PartsOrderModalResponseDTO> getByTicketPartId(@PathVariable Integer ticketPartId) {
        return ResponseEntity.ok(partsOrderService.getByTicketPartId(ticketPartId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PartsOrderModalResponseDTO> getById(@PathVariable Integer orderId) {
        return ResponseEntity.ok(partsOrderService.getById(orderId));
    }

    /**
     * Legacy lookup only — does not create orders. Prefer POST /create on Save.
     */
    @PostMapping("/open")
    public ResponseEntity<PartsOrderModalResponseDTO> open(@RequestBody PartsOrderOpenRequestDTO request) {
        return ResponseEntity.ok(partsOrderService.open(request));
    }

    /**
     * Create order + master lines on Save (one parts_order for the ticket part).
     */
    @PostMapping("/create")
    public ResponseEntity<PartsOrderModalResponseDTO> create(@RequestBody PartsOrderSaveRequestDTO request) {
        return new ResponseEntity<>(partsOrderService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{orderId}/save")
    public ResponseEntity<PartsOrderModalResponseDTO> save(
            @PathVariable Integer orderId,
            @RequestBody PartsOrderSaveRequestDTO request) {
        return ResponseEntity.ok(partsOrderService.save(orderId, request));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<PartsOrderModalResponseDTO> updateStatus(
            @PathVariable Integer orderId,
            @RequestBody PartsOrderStatusRequestDTO request) {
        return ResponseEntity.ok(partsOrderService.updateStatus(orderId, request));
    }
}
