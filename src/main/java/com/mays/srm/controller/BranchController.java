package com.mays.srm.controller;

import com.mays.srm.entity.Branch;
import com.mays.srm.service.BranchService;
import com.mays.srm.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/branches")
public class BranchController extends AbstractController<Branch, Integer> {

    @Autowired
    private BranchService service;

    @Override
    protected GenericService<Branch, Integer> getService() {
        return service;
    }
}