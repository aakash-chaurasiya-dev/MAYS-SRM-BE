package com.mays.srm.user.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import com.mays.srm.user.entities.Vendor;
import java.util.Optional;

public interface VendorDao extends JpaRepository<Vendor, Integer> {
    Optional<Vendor> findByMobileNo(String mobileNo);
    Optional<Vendor> findByEmail(String email);
}
