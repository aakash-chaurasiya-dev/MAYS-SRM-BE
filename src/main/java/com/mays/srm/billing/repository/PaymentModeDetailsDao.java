package com.mays.srm.billing.repository;
import com.mays.srm.billing.entities.PaymentModeDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentModeDetailsDao extends JpaRepository<PaymentModeDetails, Integer> {
}
