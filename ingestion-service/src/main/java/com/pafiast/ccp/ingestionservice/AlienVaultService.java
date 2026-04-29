package com.pafiast.ccp.ingestionservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.kafka.core.KafkaTemplate;

@Service
public class AlienVaultService {

    @Value("${alienvault.api.key}")
    private String apiKey;

    @Value("${alienvault.api.url}")
    private String apiUrl;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RestTemplate restTemplate;

    public AlienVaultService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = new RestTemplate();
    }

    public String fetchAndSendToKafka() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-OTX-API-KEY", apiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Fetch subscribed pulses (you can change the endpoint later for specific searches)
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String alienVaultJson = response.getBody();

            // Send to the same raw-iocs topic
            kafkaTemplate.send("raw-iocs", alienVaultJson);

            System.out.println("✅ Successfully fetched AlienVault data and sent to Kafka.");
            return alienVaultJson;

        } catch (Exception e) {
            System.err.println("❌ Error fetching from AlienVault: " + e.getMessage());
            return null;
        }
    }
}