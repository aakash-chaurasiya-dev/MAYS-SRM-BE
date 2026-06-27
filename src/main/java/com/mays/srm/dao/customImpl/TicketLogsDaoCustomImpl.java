package com.mays.srm.dao.customImpl;

import java.util.List;

import com.mays.srm.dao.custom.TicketLogsDaoCustom;
import com.mays.srm.dto.responseDTO.TicketLogsDTO.TicketLogsSummaryResponseDTO;
import com.mays.srm.entity.TicketLogs;

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
        String jpql = "SELECT NEW com.mays.srm.dto.responseDTO.TicketLogsDTO.TicketLogsSummaryResponseDTO(" +
                "m.employeeName, s.statusName, t.modificationDate, t.assignorRemarks, ar.employeeName, ae.employeeName) " +
                "FROM TicketLogs t " +
                "LEFT JOIN t.modifiedBy m " +
                "LEFT JOIN t.newStatus s " +
                "LEFT JOIN t.assignorEmployee ar " +
                "LEFT JOIN t.assigneeEmployee ae " +
                "WHERE t.ticket.ticketId = :ticketId ORDER BY t.modificationDate DESC";
        TypedQuery<com.mays.srm.dto.responseDTO.TicketLogsDTO.TicketLogsSummaryResponseDTO> query = entityManager.createQuery(jpql, com.mays.srm.dto.responseDTO.TicketLogsDTO.TicketLogsSummaryResponseDTO.class);
        query.setParameter("ticketId", ticketId);
        query.setMaxResults(3);
        return query.getResultList();
    }
}
