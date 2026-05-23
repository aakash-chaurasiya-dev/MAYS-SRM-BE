package com.mays.srm.service.impl;

import com.mays.srm.dao.core.PaymentModeDetailsDao;
import com.mays.srm.entity.PaymentModeDetails;
import com.mays.srm.service.PaymentModeDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentModeDetailsServiceImpl implements PaymentModeDetailsService {

    private final PaymentModeDetailsDao paymentModeDetailsDao;

    @Autowired
    public PaymentModeDetailsServiceImpl(PaymentModeDetailsDao paymentModeDetailsDao) {
        this.paymentModeDetailsDao = paymentModeDetailsDao;
    }

    @Override
    public PaymentModeDetails create(PaymentModeDetails entity) {
        return paymentModeDetailsDao.save(entity);
    }

    @Override
    public Optional<PaymentModeDetails> getById(Integer id) {
        return paymentModeDetailsDao.findById(id);
    }

    @Override
    public List<PaymentModeDetails> getAll() {
        return paymentModeDetailsDao.findAll();
    }

    @Override
    public PaymentModeDetails update(PaymentModeDetails entity) {
        return paymentModeDetailsDao.save(entity);
    }

    @Override
    public void delete(Integer id) {
        paymentModeDetailsDao.deleteById(id);
    }
}