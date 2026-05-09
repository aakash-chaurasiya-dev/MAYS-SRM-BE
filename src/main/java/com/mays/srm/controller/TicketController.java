package com.mays.srm.controller;

import com.mays.srm.dto.TicketPatchDTO;
import com.mays.srm.entity.Ticket;
import com.mays.srm.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        return ResponseEntity.ok(ticketService.createTicket(ticket));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicket(@PathVariable int id) {
        Optional<Ticket> ticket = ticketService.getTicket(id);
        return ticket.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Ticket>> getAllTicketsOfUser(@PathVariable String userId) {
        return ResponseEntity.ok(ticketService.getAllTicketsOfUser(userId));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<Ticket>> getAllTicketsOfBranch(@PathVariable int branchId) {
        return ResponseEntity.ok(ticketService.getAllTicketsOfBranch(branchId));
    }

    @GetMapping("/status/{statusId}")
    public ResponseEntity<List<Ticket>> getAllTicketsOfStatus(@PathVariable int statusId) {
        return ResponseEntity.ok(ticketService.getAllTicketsOfStatus(statusId));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Ticket>> getAllTicketsOfEmployee(@PathVariable int employeeId) {
        return ResponseEntity.ok(ticketService.getAllTicketsOfEmployee(employeeId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable int id, @RequestBody TicketPatchDTO body) {
        return ResponseEntity.ok(ticketService.updateTicket(id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable int id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok().build();
    }
}
