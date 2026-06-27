package com.mays.srm.dao.custom;
import com.mays.srm.billing.entities.ChargeType;
import java.util.Optional;

public interface ChargeTypeDaoCustom {
    Optional<ChargeType> getChargeTypeByName(String chargeName);
}
