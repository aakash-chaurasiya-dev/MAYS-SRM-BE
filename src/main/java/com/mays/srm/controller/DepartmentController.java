package com.mays.srm.controller;

import com.mays.srm.entity.Department;
import com.mays.srm.service.DepartmentService;
import com.mays.srm.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController extends AbstractController<Department, Integer> {

    @Autowired
    private DepartmentService service;

    @Override
    protected GenericService<Department, Integer> getService() {
        return service;
    }
}