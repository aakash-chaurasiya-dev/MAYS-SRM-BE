package com.mays.srm.timetracking.integration;

import com.mays.srm.organization.entities.Status;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.user.entities.Employee;

public interface TicketTimeTrackingFacade {

    void onTicketCreated(Ticket ticket);

    void onTicketUpdated(Ticket updatedTicket, Status oldStatus, Employee oldAssignee, String holdReason);
}
