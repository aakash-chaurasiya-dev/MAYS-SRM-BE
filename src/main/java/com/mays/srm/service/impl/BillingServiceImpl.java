package com.mays.srm.service.impl;

import com.mays.srm.dao.core.BillingDao;
import com.mays.srm.entity.Billing;
import com.mays.srm.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BillingServiceImpl implements BillingService {

    private final BillingDao repository;

    @Autowired
    public BillingServiceImpl(BillingDao repository) {
        this.repository = repository;
    }

    @Override
    public Billing create(Billing entity) {

        return repository.save(entity);
    }

    @Override
    public Optional<Billing> getById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<Billing> getAll() {
        return repository.findAll();
    }

    @Override
    public Billing update(Billing entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
