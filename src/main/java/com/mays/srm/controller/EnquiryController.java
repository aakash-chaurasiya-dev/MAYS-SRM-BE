package com.mays.srm.controller;

import com.mays.srm.entity.Enquiry;
import com.mays.srm.service.EnquiryService;
import com.mays.srm.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enquiries")
public class EnquiryController extends AbstractController<Enquiry, Integer> {

    @Autowired
    private EnquiryService service;

    @Override
    protected GenericService<Enquiry, Integer> getService() {
        return service;
    }
}