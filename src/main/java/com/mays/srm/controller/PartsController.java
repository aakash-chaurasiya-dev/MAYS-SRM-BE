package com.mays.srm.controller;

import com.mays.srm.entity.Parts;
import com.mays.srm.service.GenericService;
import com.mays.srm.service.PartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parts")
public class PartsController extends AbstractController<Parts, Integer> {

    @Autowired
    private PartsService service;

    @Override
    protected GenericService<Parts, Integer> getService() {
        return service;
    }
}