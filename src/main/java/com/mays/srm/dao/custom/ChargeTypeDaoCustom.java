package com.mays.srm.dao.custom;

import com.mays.srm.entity.ChargeType;
import java.util.Optional;

public interface ChargeTypeDaoCustom {
    Optional<ChargeType> getChargeTypeByName(String chargeName);
}
