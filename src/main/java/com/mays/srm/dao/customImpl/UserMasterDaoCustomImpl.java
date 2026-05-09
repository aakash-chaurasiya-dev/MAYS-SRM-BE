package com.mays.srm.dao.customImpl;

import com.mays.srm.dao.custom.UserMasterDaoCustom;
import com.mays.srm.entity.Employee;
import com.mays.srm.entity.UserMaster;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserMasterDaoCustomImpl implements UserMasterDaoCustom {

    private final EntityManager entityManager;

    @Autowired
    public UserMasterDaoCustomImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public List<UserMaster> findByBranchName(String branchName) {
        TypedQuery<UserMaster> query = entityManager.createQuery(
                "SELECT u FROM User_Master u WHERE u.branch.branchName = :branchName",
                UserMaster.class
        );

        query.setParameter("branchName", branchName);

        return query.getResultList();
    }
}
