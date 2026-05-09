package com.mays.srm.controller;

import com.mays.srm.entity.ServiceCharges;
import com.mays.srm.service.GenericService;
import com.mays.srm.service.ServiceChargesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service-charges")
public class ServiceChargesController extends AbstractController<ServiceCharges, Integer> {

    @Autowired
    private ServiceChargesService service;

    @Override
    protected GenericService<ServiceCharges, Integer> getService() {
        return service;
    }
}