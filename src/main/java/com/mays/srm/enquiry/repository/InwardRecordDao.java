package com.mays.srm.enquiry.repository;

import com.mays.srm.enquiry.entities.InwardRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InwardRecordDao extends JpaRepository<InwardRecord, Integer> {
}
