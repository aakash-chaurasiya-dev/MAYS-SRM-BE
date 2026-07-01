package com.mays.srm.user.repository;
import com.mays.srm.user.entities.UserMaster;
import com.mays.srm.user.repository.UserMasterDao;

import java.util.List;
import java.util.Optional;

public interface UserMasterDaoCustom{
    List<UserMaster> findByBranchName(String branchName);
}

