package com.pafiast.ccp.extractionservice;

import org.springframework.stereotype.Service;

@Service
public class RankingService {

    // Simulates an API call to VirusTotal, GreyNoise, etc.
    public Integer getEnrichedSeverityScore(String ipAddress) {
        System.out.println("🔍 Querying External Ranking API for IP: " + ipAddress);

        // In a real scenario, this would use a RestTemplate to hit an API.
        // For demonstration, we simulate a latency and a score.
        try {
            Thread.sleep(10); // Simulate network delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Generate a deterministic but variable score based on the IP string
        int hash = Math.abs(ipAddress.hashCode());
        int simulatedScore = 50 + (hash % 51); // Score between 50 and 100

        System.out.println("🎯 Received Enriched Score: " + simulatedScore + "/100");
        return simulatedScore;
    }
}