package com.mays.srm.timetracking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mays.srm.timetracking.entities.SlaHoldRequest;
import com.mays.srm.timetracking.enums.HoldRequestStatus;

@Repository
public interface SlaHoldRequestRepository extends JpaRepository<SlaHoldRequest, Long> {

    Optional<SlaHoldRequest> findFirstByTicketTicketIdAndStatusOrderByRequestedAtDesc(Integer ticketId, HoldRequestStatus status);

    List<SlaHoldRequest> findByStatusOrderByRequestedAtAsc(HoldRequestStatus status);

    List<SlaHoldRequest> findByTicketTicketIdOrderByRequestedAtDesc(Integer ticketId);
}
