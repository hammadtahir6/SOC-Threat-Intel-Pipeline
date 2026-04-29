package com.pafiast.ccp.ingestionservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AbuseIPDBService {

    // This grabs the key from your application.properties
    @Value("${abuseipdb.api.key}")
    private String apiKey;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RestTemplate restTemplate;

    public AbuseIPDBService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = new RestTemplate();
    }

    public void fetchAndSendToKafka() {
        // The AbuseIPDB Blacklist URL
        String url = "https://api.abuseipdb.com/api/v2/blacklist?confidenceMinimum=90";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Key", apiKey);
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            System.out.println("Fetching data from AbuseIPDB...");
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // Send the JSON to Kafka
            kafkaTemplate.send("raw-iocs", response.getBody());
            System.out.println("✅ Successfully fetched AbuseIPDB data and sent to Kafka.");

        } catch (Exception e) {
            System.err.println("❌ Error fetching from AbuseIPDB: " + e.getMessage());
        }
    }
}