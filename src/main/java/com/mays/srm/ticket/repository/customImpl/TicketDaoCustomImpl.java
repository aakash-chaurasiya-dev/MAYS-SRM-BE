package com.mays.srm.ticket.repository.customImpl;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.ticket.repository.custom.TicketDaoCustom;
import com.mays.srm.ticket.dto.resDTO.TicketDashboardTicketStatsResponseDTO;
import com.mays.srm.ticket.dto.resDTO.TicketDashboardDepartmentTicketCountDTO;
import com.mays.srm.ticket.dto.resDTO.TicketDashboardResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Repository
public class TicketDaoCustomImpl implements TicketDaoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<TicketDashboardResponseDTO> getAllTicketDashboard(Pageable pageable) {
        // Use JPA Constructor Expression to instantiate the DTO directly inside the
        // query!
        // This solves the architectural issue:
        // 1. Fetches only specific columns (performance).
        // 2. Returns fully typed DTOs (no Object[] array bleeding).
        // 3. No MapperService dependency inside the DAO.
        String queryString = "SELECT new com.mays.srm.ticket.dto.resDTO.TicketDashboardResponseDTO(" +
                "t.ticketId, u.firstName, u.lastName, d.serialNo, b.branchName, s.statusName, dept.departmentName, t.createdDate) "
                +
                "FROM Ticket t " +
                "LEFT JOIN t.userMaster u " +
                "LEFT JOIN t.device d " +
                "LEFT JOIN t.ticketBranch b " +
                "LEFT JOIN t.ticketStatus s " +
                "LEFT JOIN t.employee e " +
                "LEFT JOIN e.department dept " +
                "ORDER BY t.createdDate DESC";

        // Execute query natively mapping to DTO
        TypedQuery<TicketDashboardResponseDTO> q = entityManager.createQuery(queryString,
                TicketDashboardResponseDTO.class);
        q.setFirstResult((int) pageable.getOffset());
        q.setMaxResults(pageable.getPageSize());

        List<TicketDashboardResponseDTO> resultList = q.getResultList();
        List<TicketDashboardResponseDTO> content = resultList != null ? new java.util.ArrayList<>(resultList)
                : new java.util.ArrayList<>();

        // Fetch the total count of tickets for correct Pagination
        Query countQ = entityManager.createQuery("SELECT COUNT(t) FROM Ticket t");
        long total = (Long) countQ.getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public TicketDashboardTicketStatsResponseDTO getDashboardTicketStats() {
        String statsQuery = "SELECT new com.mays.srm.ticket.dto.resDTO.TicketDashboardDepartmentTicketCountDTO(" +
                "dept.departmentName, COUNT(t.ticketId)) " +
                "FROM Ticket t " +
                "LEFT JOIN t.employee e " +
                "LEFT JOIN e.department dept " +
                "GROUP BY dept.departmentName";

        TypedQuery<TicketDashboardDepartmentTicketCountDTO> q = 
                entityManager.createQuery(statsQuery, TicketDashboardDepartmentTicketCountDTO.class);
        
        List<TicketDashboardDepartmentTicketCountDTO> departmentCounts = q.getResultList();

        Query countQ = entityManager.createQuery("SELECT COUNT(t) FROM Ticket t");
        long total = (Long) countQ.getSingleResult();

        return new TicketDashboardTicketStatsResponseDTO(total, departmentCounts);
    }

    @Override
    public Page<TicketDashboardResponseDTO> getTicketsByDepartmentDashboard(String departmentName, Pageable pageable) {
        String baseQuery = "SELECT new com.mays.srm.ticket.dto.resDTO.TicketDashboardResponseDTO(" +
                "t.ticketId, u.firstName, u.lastName, d.serialNo, b.branchName, s.statusName, dept.departmentName, t.createdDate) " +
                "FROM Ticket t " +
                "LEFT JOIN t.userMaster u " +
                "LEFT JOIN t.device d " +
                "LEFT JOIN t.ticketBranch b " +
                "LEFT JOIN t.ticketStatus s " +
                "LEFT JOIN t.employee e " +
                "LEFT JOIN e.department dept ";

        boolean isUnassigned = "Unassigned".equalsIgnoreCase(departmentName);
        String whereClause = isUnassigned 
            ? "WHERE dept.departmentName IS NULL "
            : "WHERE dept.departmentName = :departmentName ";
            
        String orderClause = "ORDER BY t.createdDate DESC";

        TypedQuery<TicketDashboardResponseDTO> q = entityManager.createQuery(baseQuery + whereClause + orderClause, TicketDashboardResponseDTO.class);
        if (!isUnassigned) {
            q.setParameter("departmentName", departmentName);
        }
        
        q.setFirstResult((int) pageable.getOffset());
        q.setMaxResults(pageable.getPageSize());

        List<TicketDashboardResponseDTO> resultList = q.getResultList();
        List<TicketDashboardResponseDTO> content = resultList != null ? new java.util.ArrayList<>(resultList) : new java.util.ArrayList<>();

        String countQueryStr = "SELECT COUNT(t) FROM Ticket t " +
                "LEFT JOIN t.employee e " +
                "LEFT JOIN e.department dept " +
                whereClause;
        Query countQ = entityManager.createQuery(countQueryStr);
        if (!isUnassigned) {
            countQ.setParameter("departmentName", departmentName);
        }
        long total = (Long) countQ.getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }
}
