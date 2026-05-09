package com.mays.srm.controller;

import com.mays.srm.entity.DeviceModel;
import com.mays.srm.service.DeviceModelService;
import com.mays.srm.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/device-models")
public class DeviceModelController extends AbstractController<DeviceModel, Integer> {

    @Autowired
    private DeviceModelService service;

    @Override
    protected GenericService<DeviceModel, Integer> getService() {
        return service;
    }

    @GetMapping("/search")
    public ResponseEntity<DeviceModel> findByModelNameAndBrandName(
            @RequestParam String modelName, 
            @RequestParam String brandName) {
        Optional<DeviceModel> model = service.findByModelNameAndBrandName(modelName, brandName);
        if (model.isPresent()) {
            return ResponseEntity.ok(model.get());
        }
        return ResponseEntity.notFound().build();
    }
}
