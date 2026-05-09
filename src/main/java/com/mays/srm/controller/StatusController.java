package com.mays.srm.controller;

import com.mays.srm.entity.Status;
import com.mays.srm.service.GenericService;
import com.mays.srm.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statuses")
public class StatusController extends AbstractController<Status, Integer> {

    @Autowired
    private StatusService service;

    @Override
    protected GenericService<Status, Integer> getService() {
        return service;
    }
}
