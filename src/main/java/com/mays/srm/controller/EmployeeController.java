package com.mays.srm.controller;

import com.mays.srm.entity.Employee;
import com.mays.srm.service.EmployeeService;
import com.mays.srm.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController extends AbstractController<Employee, Integer> {

    @Autowired
    private EmployeeService service;

    @Override
    protected GenericService<Employee, Integer> getService() {
        return service;
    }

    @GetMapping("/mobile")
    public ResponseEntity<Employee> findByMobileNo(@RequestParam String mobileNo) {
        // Our service method throws ResourceNotFoundException automatically if not found
        Optional<Employee> employeeOpt = service.findEmployeeByMobileNo(mobileNo);
        
        if (employeeOpt.isPresent()) {
            return ResponseEntity.ok(employeeOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email")
    public ResponseEntity<Employee> findByEmail(@RequestParam String email) {
        Optional<Employee> employeeOpt = service.findEmployeeByEmail(email);
        
        if (employeeOpt.isPresent()) {
            return ResponseEntity.ok(employeeOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name")
    public ResponseEntity<List<Employee>> findByEmployeeName(@RequestParam String employeeName) {
        List<Employee> employees = service.findByEmployeeName(employeeName);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Employee>> findByDepartment(@PathVariable int departmentId) {
        List<Employee> employees = service.findEmployeeByDepartment(departmentId);
        return ResponseEntity.ok(employees);
    }
}
