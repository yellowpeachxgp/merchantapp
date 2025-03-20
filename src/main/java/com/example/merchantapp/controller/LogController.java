package com.example.merchantapp.controller;

import com.example.merchantapp.repository.OperationLogRepository;
import com.example.merchantapp.repository.LoginLogRepository;
import com.example.merchantapp.model.OperationLog;
import com.example.merchantapp.model.LoginLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogController {
    @Autowired
    private OperationLogRepository operationLogRepository;
    @Autowired
    private LoginLogRepository loginLogRepository;

    @GetMapping("/operations")
    public List<OperationLog> getOperationLogs() {
        return operationLogRepository.findAll();
    }

    @GetMapping("/logins")
    public List<LoginLog> getLoginLogs() {
        return loginLogRepository.findAll();
    }
}
