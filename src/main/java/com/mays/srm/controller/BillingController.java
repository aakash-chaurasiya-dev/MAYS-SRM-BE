package com.mays.srm.controller;

import com.mays.srm.entity.Billing;
import com.mays.srm.service.BillingService;
import com.mays.srm.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/billings")
public class BillingController extends AbstractController<Billing, Integer> {

    @Autowired
    private BillingService service;

    @Override
    protected GenericService<Billing, Integer> getService() {
        return service;
    }
}