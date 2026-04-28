package com.pafiast.ccp.threatapiservice;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
// This CrossOrigin tag is CRITICAL. It allows our HTML dashboard to securely fetch this data!
@CrossOrigin(origins = "*")
public class ThreatController {

    private final IocRepository iocRepository;

    public ThreatController(IocRepository iocRepository) {
        this.iocRepository = iocRepository;
    }

    @GetMapping("/threats")
    public List<Ioc> getAllThreats() {
        System.out.println("💻 Web Dashboard requested threat data. Sending from Database...");
        // Grab all the threats from MySQL and send them to the frontend
        return iocRepository.findAll();
    }
}