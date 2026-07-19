package com.mays.srm.user.controller;

import com.mays.srm.user.dto.UserEntryReportRequestDTO;
import com.mays.srm.user.service.UserEntryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/user-entry-reports")
public class UserEntryReportController {

    @Autowired
    private UserEntryReportService service;

    @PostMapping
    public ResponseEntity<?> saveEntry(@RequestBody UserEntryReportRequestDTO dto) {
        return ResponseEntity.ok(service.saveEntry(dto));
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayReports(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        return ResponseEntity.ok(service.getTodayReports(pageable));
    }

    @GetMapping("/range")
    public ResponseEntity<?> getReportsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        return ResponseEntity.ok(service.getReportsByDateRange(start, end, pageable));
    }

    @GetMapping("/counts")
    public ResponseEntity<?> getStatusCounts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(service.getStatusCounts(start, end));
    }
}
