package com.mays.srm.controller;

import com.mays.srm.entity.Device;
import com.mays.srm.service.DeviceService;
import com.mays.srm.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController extends AbstractController<Device, String> {

    @Autowired
    private DeviceService service;

    @Override
    protected GenericService<Device, String> getService() {
        return service;
    }

    @GetMapping("/model/{modelName}")
    public ResponseEntity<List<Device>> findByModelName(@PathVariable String modelName) {
        List<Device> devices = service.findByModelName(modelName);
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/brand/{brandName}")
    public ResponseEntity<List<Device>> findByBrandName(@PathVariable String brandName) {
        List<Device> devices = service.findByBrandName(brandName);
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/type/{deviceType}")
    public ResponseEntity<List<Device>> findByDeviceTypeName(@PathVariable String deviceType) {
        List<Device> devices = service.findByDeviceTypeName(deviceType);
        return ResponseEntity.ok(devices);
    }
}
