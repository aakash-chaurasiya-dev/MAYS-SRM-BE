package com.mays.srm.notification.repository;

import com.mays.srm.notification.entities.NotificationOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationOutboxDao extends JpaRepository<NotificationOutbox, Integer> {
    List<NotificationOutbox> findByStatus(String status);
}
