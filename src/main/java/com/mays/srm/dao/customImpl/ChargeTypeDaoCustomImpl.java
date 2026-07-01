package com.mays.srm.dao.customImpl;
import com.mays.srm.dao.custom.ChargeTypeDaoCustom;
import com.mays.srm.billing.entities.ChargeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ChargeTypeDaoCustomImpl implements ChargeTypeDaoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<ChargeType> getChargeTypeByName(String chargeName) {
        String jpql = "SELECT c FROM ChargeType c WHERE c.chargeName = :chargeName";
        TypedQuery<ChargeType> query = entityManager.createQuery(jpql, ChargeType.class);
        query.setParameter("chargeName", chargeName);
        List<ChargeType> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            return Optional.of(results.get(0));
        }
        return Optional.empty();
    }
}
