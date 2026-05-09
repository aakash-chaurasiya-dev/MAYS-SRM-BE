package com.mays.srm.dao.core;

import com.mays.srm.entity.Enquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnquiryDao extends JpaRepository<Enquiry, Integer> {
}
