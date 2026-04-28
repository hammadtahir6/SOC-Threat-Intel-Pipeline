package com.pafiast.ccp.ingestionservice;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class IngestionController {

    // This is the tool Spring uses to talk to Kafka
    private final KafkaTemplate<String, String> kafkaTemplate;
    
    public IngestionController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/ingest")
    public String fetchAndPublishThreats() {
        RestTemplate restTemplate = new RestTemplate();
        
        // 1. We are using the AbuseIPDB Blacklist API
        String apiUrl = "https://api.abuseipdb.com/api/v2/blacklist?limit=5000";
        
        // 2. Set up the headers (You need an API key for this!)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Key", "deda159f1b9744e1a07a9959a8c3130da0aaa3310d9f0dab395970273623b26f73efd34cf4a549cf"); 
        
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // 3. Make the request to the external threat API
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
            String rawJson = response.getBody();

            // 4. Send that raw JSON straight to your Kafka Topic
            kafkaTemplate.send("raw-iocs", rawJson);

            return "Success! Threat data fetched and sent to Kafka topic: raw-iocs";
            
        } catch (Exception e) {
            return "Oops, something went wrong: " + e.getMessage();
        }
    }
}