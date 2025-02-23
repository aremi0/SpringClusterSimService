package com.aremi.springclustersimservice.controller;

import com.aremi.springclustersimservice.service.ExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ExecutionController {

    private final ExecutionService service;

    @GetMapping("/exec")
    public ResponseEntity<String> exec() {
        log.info("exec:: started");
        return service.exec();
    }
}
