package com.pafiast.ccp.ingestionservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IngestionController {

    private final AbuseIPDBService abuseIPDBService;
    private final AlienVaultService alienVaultService;

    // Inject both services
    public IngestionController(AbuseIPDBService abuseIPDBService, AlienVaultService alienVaultService) {
        this.abuseIPDBService = abuseIPDBService;
        this.alienVaultService = alienVaultService;
    }

    @GetMapping("/ingest")
    public String startIngestion() {
        System.out.println("📡 Ingestion triggered! Fetching from multiple sources...");

        // Fetch from AbuseIPDB
        abuseIPDBService.fetchAndSendToKafka();

        // Fetch from AlienVault
        alienVaultService.fetchAndSendToKafka();

        return "✅ Success! Data fetched from AbuseIPDB and AlienVault and sent to Kafka pipeline!";
    }
}