package com.mays.srm.enquiry.repository;

import com.mays.srm.enquiry.entities.OutwardRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OutwardRecordDao extends JpaRepository<OutwardRecord, Integer> {
    Optional<OutwardRecord> findByTicketTicketId(Integer ticketId);
}
