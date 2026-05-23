package com.mays.srm.dao.core;

import com.mays.srm.entity.PaymentModeDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentModeDetailsDao extends JpaRepository<PaymentModeDetails, Integer> {
}
