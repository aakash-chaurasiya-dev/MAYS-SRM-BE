package com.mays.srm.timetracking.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mays.srm.organization.entities.Status;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.timetracking.service.TicketTimeTrackingService;
import com.mays.srm.user.entities.Employee;

@Service
public class TicketTimeTrackingFacadeImpl implements TicketTimeTrackingFacade {

    @Autowired
    private TicketTimeTrackingService ticketTimeTrackingService;

    @Override
    public void onTicketCreated(Ticket ticket) {
        ticketTimeTrackingService.startTrackingForNewTicket(ticket);
    }

    @Override
    public void onTicketUpdated(Ticket updatedTicket, Status oldStatus, Employee oldAssignee, String holdReason) {
        ticketTimeTrackingService.handleTrackingUpdates(updatedTicket, oldStatus, oldAssignee, holdReason);
    }
}
