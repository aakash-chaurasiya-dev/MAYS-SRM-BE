package com.mays.srm.user.repository.impl;

import com.mays.srm.user.entities.UserEntryReport;
import com.mays.srm.user.repository.UserEntryReportDaoCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import com.mays.srm.util.RestPageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public class UserEntryReportDaoImpl implements UserEntryReportDaoCustom {

    private final EntityManager entityManager;

    @Autowired
    public UserEntryReportDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<UserEntryReport> findByDateRange(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable) {
        TypedQuery<UserEntryReport> query = entityManager.createQuery(
                "SELECT r FROM UserEntryReport r WHERE r.entryDate >= :startOfDay AND r.entryDate < :endOfDay ORDER BY r.entryDate DESC",
                UserEntryReport.class
        );
        query.setParameter("startOfDay", startOfDay);
        query.setParameter("endOfDay", endOfDay);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        
        List<UserEntryReport> content = query.getResultList();

        TypedQuery<Long> countQuery = entityManager.createQuery(
                "SELECT COUNT(r) FROM UserEntryReport r WHERE r.entryDate >= :startOfDay AND r.entryDate < :endOfDay",
                Long.class
        );
        countQuery.setParameter("startOfDay", startOfDay);
        countQuery.setParameter("endOfDay", endOfDay);
        long total = countQuery.getSingleResult();

        return new RestPageImpl<>(content, pageable, total);
    }

    @Override
    public List<UserEntryReport> findAllByDateRange(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        TypedQuery<UserEntryReport> query = entityManager.createQuery(
                "SELECT r FROM UserEntryReport r WHERE r.entryDate >= :startOfDay AND r.entryDate < :endOfDay",
                UserEntryReport.class
        );
        query.setParameter("startOfDay", startOfDay);
        query.setParameter("endOfDay", endOfDay);
        
        return query.getResultList();
    }
}
