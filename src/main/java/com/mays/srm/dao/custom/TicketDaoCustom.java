package com.mays.srm.dao.custom;

import com.mays.srm.entity.Ticket;
import com.mays.srm.dto.TicketPatchDTO;

public interface TicketDaoCustom {
    Ticket updateTicket(int id, TicketPatchDTO dto);
}
