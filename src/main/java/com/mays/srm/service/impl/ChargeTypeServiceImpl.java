package com.mays.srm.service.impl;

import com.mays.srm.dao.core.ChargeTypeDao;
import com.mays.srm.entity.ChargeType;
import com.mays.srm.service.ChargeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChargeTypeServiceImpl implements ChargeTypeService {

    @Autowired
    private ChargeTypeDao chargeTypeDao;

    @Override
    public ChargeType createChargeType(ChargeType chargeType) {
        return chargeTypeDao.save(chargeType);
    }

    @Override
    public ChargeType getChargeTypeById(Integer id) {
        Optional<ChargeType> chargeType = chargeTypeDao.findById(id);
        return chargeType.orElse(null);
    }

    @Override
    public List<ChargeType> getAllChargeTypes() {
        return chargeTypeDao.findAll();
    }

    @Override
    public void deleteChargeType(Integer id) {
        chargeTypeDao.deleteById(id);
    }
}
