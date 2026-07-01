package com.mays.srm.ticket.repository.customImpl;

import java.util.List;

import com.mays.srm.ticket.repository.custom.TicketLogsDaoCustom;
import com.mays.srm.ticket.entities.TicketLogs;
import com.mays.srm.ticket.dto.resDTO.TicketLogsSummaryResponseDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

public class TicketLogsDaoCustomImpl implements TicketLogsDaoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<TicketLogs> getDetailedTicketLogs(Integer ticketId) {
        String jpql = "SELECT t FROM TicketLogs t WHERE t.ticket.ticketId = :ticketId ORDER BY t.modificationDate DESC";
        TypedQuery<TicketLogs> query = entityManager.createQuery(jpql, TicketLogs.class);
        query.setParameter("ticketId", ticketId);
        return query.getResultList();
    }

    @Override
    public List<TicketLogsSummaryResponseDTO> getTicketLogsForTicketDetail(Integer ticketId) {
        String jpql = "SELECT NEW com.mays.srm.ticket.dto.resDTO.TicketLogsSummaryResponseDTO(" +
                "m.employeeName, s.statusName, t.modificationDate, t.assignorRemarks, ar.employeeName, ae.employeeName) " +
                "FROM TicketLogs t " +
                "LEFT JOIN t.modifiedBy m " +
                "LEFT JOIN t.newStatus s " +
                "LEFT JOIN t.assignorEmployee ar " +
                "LEFT JOIN t.assigneeEmployee ae " +
                "WHERE t.ticket.ticketId = :ticketId ORDER BY t.modificationDate DESC";
        TypedQuery<TicketLogsSummaryResponseDTO> query = entityManager.createQuery(jpql, TicketLogsSummaryResponseDTO.class);
        query.setParameter("ticketId", ticketId);
        query.setMaxResults(3);
        return query.getResultList();
    }
}
