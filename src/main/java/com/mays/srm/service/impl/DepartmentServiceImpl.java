package com.mays.srm.service.impl;

import com.mays.srm.dao.core.DepartmentDao;
import com.mays.srm.entity.Department;
import com.mays.srm.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentDao repository;

    @Autowired
    public DepartmentServiceImpl(DepartmentDao repository) {
        this.repository = repository;
    }

    @Override
    public Department create(Department entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<Department> getById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<Department> getAll() {
        return repository.findAll();
    }

    @Override
    public Department update(Department entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
