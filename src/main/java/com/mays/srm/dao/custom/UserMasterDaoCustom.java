package com.mays.srm.dao.custom;

import com.mays.srm.dao.core.UserMasterDao;
import com.mays.srm.entity.UserMaster;

import java.util.List;
import java.util.Optional;

public interface UserMasterDaoCustom{
    List<UserMaster> findByBranchName(String branchName);
}
