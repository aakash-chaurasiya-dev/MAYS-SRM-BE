package com.mays.srm.billing.repository;
import com.mays.srm.billing.entities.Billing;
import java.util.List;
import java.util.Optional;

public interface BillingDaoCustom {
    List<Billing> getChargesByTicketId(Integer ticketId);
    Optional<Billing> getChargeByTicketIdAndChargeName(Integer ticketId, String chargeName);
    List<Billing> getChargesByChargeName(String chargeName);
}

