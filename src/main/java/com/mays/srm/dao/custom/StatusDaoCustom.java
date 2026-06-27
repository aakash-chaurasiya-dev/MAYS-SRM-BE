package com.mays.srm.dao.custom;
import com.mays.srm.organization.entities.Status;
import java.util.List;
import java.util.Optional;

public interface StatusDaoCustom {
    List<Status> getStatusesByType(String statusType);
    Optional<Status> getStatusByNameAndType(String statusName, String statusType);
}
