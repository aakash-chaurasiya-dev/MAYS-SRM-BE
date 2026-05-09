package com.mays.srm.service.impl;

import com.mays.srm.dao.core.TicketLogsDao;
import com.mays.srm.entity.TicketLogs;
import com.mays.srm.service.TicketLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketLogsServiceImpl implements TicketLogsService {

    private final TicketLogsDao repository;

    @Autowired
    public TicketLogsServiceImpl(TicketLogsDao repository) {
        this.repository = repository;
    }

    @Override
    public TicketLogs create(TicketLogs entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<TicketLogs> getById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<TicketLogs> getAll() {
        return repository.findAll();
    }

    @Override
    public TicketLogs update(TicketLogs entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
