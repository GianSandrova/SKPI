package com.skpijtk.springboot_boilerplate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    private static final Logger log = LoggerFactory.getLogger(PingController.class);

    /**
     * A simple endpoint to check if the application is responsive.
     * Responds with "pong" to indicate it's alive.
     *
     * @return ResponseEntity containing the string "pong" with an OK status.
     */
    @GetMapping("/ping") // Maps HTTP GET requests for /ping to this method
    public ResponseEntity<String> ping() {
        log.info("Received ping request"); // Good practice to log requests
        return ResponseEntity.ok("pong"); // Returns "pong" with HTTP 200 OK status
    }
}
