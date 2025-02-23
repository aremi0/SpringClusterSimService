package com.aremi.springclustersimservice.controller;

import com.aremi.springclustersimservice.service.ExecutionExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ExecutionController {

    private final ExecutionExecution service;

    /**
     * Metodo generico per bloccare il server per qualche secondo
     * @param authorization
     * @return
     */
    @GetMapping("/exec")
    public ResponseEntity<String> exec(@RequestHeader(value = "Authorization", required = false) String authorization) {
        log.info("exec:: started with params: {}", authorization);
        return service.startExec();
    }
}
