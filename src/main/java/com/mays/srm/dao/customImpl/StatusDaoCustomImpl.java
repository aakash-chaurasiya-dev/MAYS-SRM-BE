package com.mays.srm.dao.customImpl;

import com.mays.srm.dao.custom.StatusDaoCustom;
import com.mays.srm.entity.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StatusDaoCustomImpl implements StatusDaoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Status> getStatusesByType(String statusType) {
        String jpql = "SELECT s FROM Status s WHERE LOWER(s.statusType) = LOWER(:statusType)";
        TypedQuery<Status> query = entityManager.createQuery(jpql, Status.class);
        query.setParameter("statusType", statusType);
        return query.getResultList();
    }

    @Override
    public Optional<Status> getStatusByNameAndType(String statusName, String statusType) {
        String jpql = "SELECT s FROM Status s WHERE s.statusName = :statusName AND LOWER(s.statusType) = LOWER(:statusType)";
        TypedQuery<Status> query = entityManager.createQuery(jpql, Status.class);
        query.setParameter("statusName", statusName);
        query.setParameter("statusType", statusType);
        List<Status> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            return Optional.of(results.get(0));
        }
        return Optional.empty();
    }
}
