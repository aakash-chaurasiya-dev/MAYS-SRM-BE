package com.mays.srm.controller;

import com.mays.srm.entity.DeviceType;
import com.mays.srm.service.DeviceTypeService;
import com.mays.srm.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/device-types")
public class DeviceTypeController extends AbstractController<DeviceType, Integer> {

    @Autowired
    private DeviceTypeService service;

    @Override
    protected GenericService<DeviceType, Integer> getService() {
        return service;
    }
}