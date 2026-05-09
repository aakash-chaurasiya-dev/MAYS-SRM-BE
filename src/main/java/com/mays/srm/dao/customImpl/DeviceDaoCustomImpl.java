package com.mays.srm.dao.customImpl;

import com.mays.srm.dao.custom.DeviceDaoCustom;
import com.mays.srm.entity.Device;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DeviceDaoCustomImpl implements DeviceDaoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Device> findByModelName(String modelName) {
        String jpql = "SELECT d FROM Device d WHERE LOWER(d.model.modelName) Like LOWER(:modelName)";
        TypedQuery<Device> query = entityManager.createQuery(jpql, Device.class);
        query.setParameter("modelName","%"+ modelName + "%");
        return query.getResultList();
    }

    @Override
    public List<Device> findByBrandName(String brandName) {
        String jpql = "SELECT d FROM Device d WHERE LOWER(d.model.brand.brandName) Like LOWER(:brandName)";
        TypedQuery<Device> query = entityManager.createQuery(jpql, Device.class);
        query.setParameter("brandName", "%"+brandName+"%");
        return query.getResultList();
    }

    @Override
    public List<Device> findByDeviceTypeName(String deviceType) {
        // Now device type is linked through the Brand via the Model!
        String jpql = "SELECT d FROM Device d WHERE LOWER(d.model.brand.deviceType.deviceTypeName) Like LOWER(:deviceType)";
        TypedQuery<Device> query = entityManager.createQuery(jpql, Device.class);
        query.setParameter("deviceType","%"+ deviceType +"%");
        return query.getResultList();
    }
}
