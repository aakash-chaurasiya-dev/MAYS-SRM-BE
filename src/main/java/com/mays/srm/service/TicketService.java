package com.mays.srm.service;

import com.mays.srm.entity.Ticket;
import com.mays.srm.dto.TicketPatchDTO;
import java.util.List;
import java.util.Optional;

public interface TicketService {
    Optional<Ticket> getTicket(int id);
    List<Ticket> getAllTickets();
    List<Ticket> getAllTicketsOfUser(String userId);
    List<Ticket> getAllTicketsOfBranch(int branchId);
    List<Ticket> getAllTicketsOfStatus(int statusId);
    List<Ticket> getAllTicketsOfEmployee(int employeeId);
    Ticket createTicket(Ticket ticket);
    Ticket updateTicket(int id, TicketPatchDTO dto);
    void deleteTicket(int id);
}
