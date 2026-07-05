package com.sujay.urlshortener.health;


import org.apache.coyote.Response;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthCheck {


    @GetMapping("/health")
    public ResponseEntity<?> check(){
        return new ResponseEntity<>("Working!!", HttpStatus.OK);
    }

}
