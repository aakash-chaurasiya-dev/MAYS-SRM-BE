package com.mays.srm.dao.custom;

import com.mays.srm.entity.Billing;
import java.util.List;
import java.util.Optional;

public interface BillingDaoCustom {
    List<Billing> getChargesByTicketId(Integer ticketId);
    Optional<Billing> getChargeByTicketIdAndChargeName(Integer ticketId, String chargeName);
    List<Billing> getChargesByChargeName(String chargeName);
}
