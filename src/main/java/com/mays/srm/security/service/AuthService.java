package com.mays.srm.security.service;

import com.mays.srm.user.dto.request.EmployeeRequestDTO;
import com.mays.srm.user.dto.request.UserMasterRequestDTO;
import com.mays.srm.user.dto.resDTO.EmployeeResponseDTO;
import com.mays.srm.user.dto.resDTO.UserMasterResponseDTO;
import com.mays.srm.user.entities.Employee;
import com.mays.srm.user.entities.UserMaster;
import com.mays.srm.user.repository.EmployeeDao;
import com.mays.srm.user.repository.UserMasterDao;
import com.mays.srm.user.service.EmployeeService;
import com.mays.srm.user.service.UserMasterService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private UserMasterDao userMasterDao;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserMasterService userMasterService;

    @Cacheable(value = "userProfile", key = "#mobileNo")
    public Object getCurrentUserProfile(String mobileNo) {
        // 1. Check Employee table
        Optional<Employee> employeeOpt = employeeDao.findByMobileNo(mobileNo);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            EmployeeResponseDTO dto = modelMapper.map(employee, EmployeeResponseDTO.class);
            if (employee.getDepartment() != null) {
                dto.setDepartmentName(employee.getDepartment().getDepartmentName());
            }
            return dto;
        }

        // 2. Check User table
        Optional<UserMaster> userOpt = userMasterDao.findByMobileNo(mobileNo);
        if (userOpt.isPresent()) {
            UserMaster user = userOpt.get();
            UserMasterResponseDTO dto = modelMapper.map(user, UserMasterResponseDTO.class);
            if (user.getBranch() != null) {
                dto.setBranchName(user.getBranch().getBranchName());
            }
            return dto;
        }

        return null; // Not found
    }

    @CacheEvict(value = "userProfile", key = "#mobileNo")
    @Transactional
    public Object updateCurrentUserProfile(String mobileNo, Map<String, Object> request) {
        // 1. Check Employee table
        Optional<Employee> employeeOpt = employeeDao.findByMobileNo(mobileNo);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            EmployeeRequestDTO requestDTO = new EmployeeRequestDTO();
            requestDTO.setEmployeeName((String) request.get("employeeName"));
            requestDTO.setMobileNo((String) request.get("mobileNo"));
            requestDTO.setEmail((String) request.get("email"));
            requestDTO.setAddress((String) request.get("address"));
            requestDTO.setVendor((String) request.get("vendor"));
            requestDTO.setPincode((String) request.get("pincode"));
            requestDTO.setCity((String) request.get("city"));
            
            if (employee.getDepartment() != null) {
                requestDTO.setDepartmentId(employee.getDepartment().getDepartmentId());
            }
            requestDTO.setIsActive(employee.getIsActive());

            return employeeService.update(employee.getEmployeeId(), requestDTO);
        }

        // 2. Check User table
        Optional<UserMaster> userOpt = userMasterDao.findByMobileNo(mobileNo);
        if (userOpt.isPresent()) {
            UserMaster user = userOpt.get();
            UserMasterRequestDTO requestDTO = new UserMasterRequestDTO();
            requestDTO.setFirstName((String) request.get("firstName"));
            requestDTO.setLastName((String) request.get("lastName"));
            requestDTO.setMobileNo((String) request.get("mobileNo"));
            requestDTO.setEmailId((String) request.get("emailId"));
            requestDTO.setAddress((String) request.get("address"));
            
            if (user.getBranch() != null) {
                requestDTO.setBranchId(user.getBranch().getBranchId());
            }

            return userMasterService.update(user.getUserId(), requestDTO);
        }

        return null; // Not found
    }
}
