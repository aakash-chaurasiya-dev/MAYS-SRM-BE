package com.mays.srm.controller;

import com.mays.srm.entity.Inventory;
import com.mays.srm.service.GenericService;
import com.mays.srm.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController extends AbstractController<Inventory, Integer> {

    @Autowired
    private InventoryService service;

    @Override
    protected GenericService<Inventory, Integer> getService() {
        return service;
    }
}