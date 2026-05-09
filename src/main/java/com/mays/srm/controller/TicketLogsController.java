package com.mays.srm.controller;

import com.mays.srm.entity.TicketLogs;
import com.mays.srm.service.GenericService;
import com.mays.srm.service.TicketLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ticket-logs")
public class TicketLogsController extends AbstractController<TicketLogs, Integer> {

    @Autowired
    private TicketLogsService service;

    @Override
    protected GenericService<TicketLogs, Integer> getService() {
        return service;
    }
}