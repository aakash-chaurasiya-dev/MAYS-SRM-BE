package com.mays.srm.user.repository;
import com.mays.srm.user.repository.EmployeeDaoCustom;
import com.mays.srm.user.entities.Employee;
import com.mays.srm.user.repository.EmployeeDao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeDaoCustomImpl implements EmployeeDaoCustom {

    private final EntityManager entityManager;

    @Autowired
    public EmployeeDaoCustomImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public List<Employee> findByDepartment(int departmentId) {
        TypedQuery<Employee> query = entityManager.createQuery(
                "SELECT e FROM Employee e WHERE e.department.departmentId = :deptId",
                Employee.class
        );

        query.setParameter("deptId", departmentId);

        return query.getResultList();
    }

    @Override
    public List<Employee> findByEmployeeName(String employeeName) {

        TypedQuery<Employee> query = entityManager.createQuery(
                "SELECT e FROM Employee e WHERE e.employeeName LIKE :name",
                Employee.class
        );

        query.setParameter("name", "%" + employeeName + "%");

        return query.getResultList();
    }
}


