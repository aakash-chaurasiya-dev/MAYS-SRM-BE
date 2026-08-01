package com.mays.srm.user.repository;

import com.mays.srm.user.entities.VendorUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorUserDao extends JpaRepository<VendorUser, Integer> {
    List<VendorUser> findByVendorId(Integer vendorId);
    Page<VendorUser> findByVendorId(Integer vendorId, Pageable pageable);
}
