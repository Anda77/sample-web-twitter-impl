package com.andreiruse.sampleWebTwitterImpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Entry point for this sample application. Depends on Spring Boot, and requires no other configuration.
 * Starts a web server on the local machine, on port 8080.
 * Automatically wires the available controllers under the com.andreiruse.hsbc.codechallenge.controllers package, which
 * as of now, include MessageController and UsersController.
 * <p>
 * As this class is also tagged with @{@link RestController}, it is a controller on its own, exposing the /status endpoint.
 * <p>
 * List of available endpoints:
 * GET /users/{username}/wall
 * POST /users/{username}/follow/{who}
 * GET /users/{username}/timeline
 * GET /users/{username}/followers
 * POST /users/{username}
 * POST /message
 * GET /status
 */
@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = "com.andreiruse.sampleWebTwitterImpl.controller")
@RestController
public class ServerRunner {

    /**
     * Start point for the application
     *
     * @param args CLI arguments. Not used
     */
    public static void main(String[] args) {
        SpringApplication.run(ServerRunner.class, args);
    }

    /**
     * This handles the data store that backs the entire application.
     * This store is an implementation of the {@link DataStore} interface, and is defaulted to {@link InMemoryDataStore}.
     * <p>
     * The wiring between this class and the 2 controllers is done by the framework.
     *
     * @return an instance of the data store
     */
    @Bean
    public DataStore dataStore() {
        return new InMemoryDataStore();
    }

    /**
     * This is only used in the Integration test, to wait until the server is up and running, before performing calls.
     *
     * @return HTTP 200, as long as the server is running
     */
    @RequestMapping("/status")
    ResponseEntity serverStatus() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
