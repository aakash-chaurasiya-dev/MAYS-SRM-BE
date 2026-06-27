package com.mays.srm.service.impl.TicketServiceImplementations;

import com.mays.srm.dao.core.BillingDao;
import com.mays.srm.dao.core.ChargeTypeDao;
import com.mays.srm.dao.core.StatusDao;
import com.mays.srm.entity.Billing;
import com.mays.srm.entity.ChargeType;
import com.mays.srm.entity.Status;
import com.mays.srm.entity.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TicketBillingService {

    private final BillingDao billingDao;
    private final ChargeTypeDao chargeTypeDao;
    private final StatusDao statusDao;

    @Autowired
    public TicketBillingService(BillingDao billingDao, ChargeTypeDao chargeTypeDao, StatusDao statusDao) {
        this.billingDao = billingDao;
        this.chargeTypeDao = chargeTypeDao;
        this.statusDao = statusDao;
    }

    /**
     * Ensures that a Final Charge billing record exists for the ticket
     */
    public void ensureFinalChargeExists(Ticket ticket) {
        Optional<Billing> existingCharge = billingDao.getChargeByTicketIdAndChargeName(ticket.getTicketId(),
                "Final Charge");
        if (existingCharge.isEmpty()) {
            Billing finalCharge = new Billing();
            finalCharge.setTicket(ticket);

            ChargeType chargeType = chargeTypeDao.getChargeTypeByName("Final Charge")
                    .orElseGet(() -> {
                        ChargeType newChargeType = new ChargeType();
                        newChargeType.setChargeName("Final Charge");
                        newChargeType.setChargeDescription("Total Final Charge for the Ticket");
                        return chargeTypeDao.save(newChargeType);
                    });
            finalCharge.setChargeType(chargeType);

            Status pendingStatus = statusDao.getStatusByNameAndType("Pending", "Billing")
                    .orElseGet(() -> {
                        Status newStatus = new Status();
                        newStatus.setStatusName("Pending");
                        newStatus.setStatusType("Billing");
                        newStatus.setStatusDescription("Pending Billing Status");
                        return statusDao.save(newStatus);
                    });
            finalCharge.setStatus(pendingStatus);

            finalCharge.setAmount(java.math.BigDecimal.ZERO);
            finalCharge.setBillingDate(LocalDateTime.now());

            billingDao.save(finalCharge);
        }
    }
}
