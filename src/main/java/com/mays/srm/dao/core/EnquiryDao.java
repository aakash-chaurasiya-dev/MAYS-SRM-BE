package com.mays.srm.dao.core;
import com.mays.srm.enquiry.entities.Enquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EnquiryDao extends JpaRepository<Enquiry, Integer> {
    List<Enquiry> findByUserUserId(Integer userId);
}
