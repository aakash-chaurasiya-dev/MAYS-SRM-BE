package com.mays.srm.service;

import com.mays.srm.entity.ChargeType;
import java.util.List;

public interface ChargeTypeService {
    ChargeType createChargeType(ChargeType chargeType);
    ChargeType getChargeTypeById(Integer id);
    List<ChargeType> getAllChargeTypes();
    void deleteChargeType(Integer id);
}
