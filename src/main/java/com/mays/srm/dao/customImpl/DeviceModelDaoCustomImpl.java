package com.mays.srm.dao.customImpl;

import com.mays.srm.dao.custom.DeviceModelDaoCustom;
import com.mays.srm.entity.DeviceModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public class DeviceModelDaoCustomImpl implements DeviceModelDaoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<DeviceModel> findByModelNameAndBrandName(String modelName, String brandName) {
        try {
            TypedQuery<DeviceModel> query = entityManager.createQuery(
                    "SELECT dm FROM DeviceModel dm " +
                            "JOIN dm.brand b " +
                            "WHERE LOWER(dm.modelName) = LOWER(:modelName) " +
                            "AND LOWER(b.brandName) = LOWER(:brandName)",
                    DeviceModel.class
            );

            query.setParameter("modelName", modelName.trim());
            query.setParameter("brandName", brandName.trim());

            DeviceModel result = query.getSingleResult();
            return Optional.of(result);

        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<DeviceModel> findByModelName(String modelName) {
        try {
            TypedQuery<DeviceModel> query = entityManager.createQuery(
                    "SELECT dm FROM DeviceModel dm WHERE LOWER(dm.modelName) Like LOWER(:modelName) ",DeviceModel.class);

            query.setParameter("modelName", modelName.trim());

            return query.getResultList();
        }
        catch (NoResultException ex) {
            return List.of();
        }
    }
}