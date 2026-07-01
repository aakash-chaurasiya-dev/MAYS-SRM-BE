package com.mays.srm.billing.repository;
import com.mays.srm.billing.repository.BillingDaoCustom;
import com.mays.srm.billing.entities.Billing;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BillingDaoCustomImpl implements BillingDaoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Billing> getChargesByTicketId(Integer ticketId) {
        String jpql = "SELECT b FROM Billing b WHERE b.ticket.ticketId = :ticketId";
        TypedQuery<Billing> query = entityManager.createQuery(jpql, Billing.class);
        query.setParameter("ticketId", ticketId);
        return query.getResultList();
    }

    @Override
    public Optional<Billing> getChargeByTicketIdAndChargeName(Integer ticketId, String chargeName) {
        String jpql = "SELECT b FROM Billing b WHERE b.ticket.ticketId = :ticketId AND b.chargeType.chargeName = :chargeName";
        TypedQuery<Billing> query = entityManager.createQuery(jpql, Billing.class);
        query.setParameter("ticketId", ticketId);
        query.setParameter("chargeName", chargeName);
        List<Billing> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            return Optional.of(results.get(0));
        }
        return Optional.empty();
    }

    @Override
    public List<Billing> getChargesByChargeName(String chargeName) {
        String jpql = "SELECT b FROM Billing b WHERE b.chargeType.chargeName = :chargeName";
        TypedQuery<Billing> query = entityManager.createQuery(jpql, Billing.class);
        query.setParameter("chargeName", chargeName);
        return query.getResultList();
    }
}


