package com.mays.srm.controller;

import com.mays.srm.entity.Brand;
import com.mays.srm.service.BrandService;
import com.mays.srm.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/brands")
public class BrandController extends AbstractController<Brand, Integer> {

    @Autowired
    private BrandService service;

    @Override
    protected GenericService<Brand, Integer> getService() {
        return service;
    }
}