package com.mays.srm.controller;

import com.mays.srm.entity.TicketType;
import com.mays.srm.service.GenericService;
import com.mays.srm.service.TicketTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ticket-types")
public class TicketTypeController extends AbstractController<TicketType, Integer> {

    @Autowired
    private TicketTypeService service;

    @Override
    protected GenericService<TicketType, Integer> getService() {
        return service;
    }
}
