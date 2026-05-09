package com.mays.srm.controller;

import com.mays.srm.entity.UserMaster;
import com.mays.srm.service.GenericService;
import com.mays.srm.service.UserMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserMasterController extends AbstractController<UserMaster, String> {

    @Autowired
    private UserMasterService service;

    @Override
    protected GenericService<UserMaster, String> getService() {
        return service;
    }

    @GetMapping("/mobile")
    public ResponseEntity<UserMaster> findByMobileNo(@RequestParam String mobileNo) {
        // Our service method throws ResourceNotFoundException automatically if not found
        // So we can safely just return what it gives us.
        UserMaster user = service.findByMobileNo(mobileNo);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email")
    public ResponseEntity<UserMaster> findByEmail(@RequestParam String email) {
        UserMaster user = service.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/firstname")
    public ResponseEntity<List<UserMaster>> findByFirstName(@RequestParam String firstName) {
        List<UserMaster> users = service.findByFirstName(firstName);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/lastname")
    public ResponseEntity<List<UserMaster>> findByLastName(@RequestParam String lastName) {
        List<UserMaster> users = service.findByLastName(lastName);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/branch/{branchName}")
    public ResponseEntity<List<UserMaster>> findByBranchName(@PathVariable String branchName) {
        List<UserMaster> users = service.findByBranchName(branchName);
        return ResponseEntity.ok(users);
    }
}
