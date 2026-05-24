package com.mays.srm.controller;

import com.mays.srm.entity.PaymentModeDetails;
import com.mays.srm.service.GenericService;
import com.mays.srm.service.PaymentModeDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paymentmodedetails")
public class PaymentModeDetailsController extends AbstractController<PaymentModeDetails, Integer> {

    @Autowired
    private PaymentModeDetailsService service;

    @Override
    protected GenericService<PaymentModeDetails, Integer> getService() {
        return service;
    }
}