package com.pafiast.ccp.extractionservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ExtractionListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final IocRepository iocRepository;
    private final RankingService rankingService;

    // Inject the Repository AND the new Ranking Service
    public ExtractionListener(IocRepository iocRepository, RankingService rankingService) {
        this.iocRepository = iocRepository;
        this.rankingService = rankingService;
    }

    @KafkaListener(topics = "raw-iocs", groupId = "extraction-group")
    public void consumeRawData(String message) {
        System.out.println("\n🔥 [Kafka Alert] New payload received. Determining source...");

        try {
            JsonNode rootNode = objectMapper.readTree(message);

            // 1. Check if it's an AbuseIPDB payload
            if (rootNode.has("data") && rootNode.path("data").isArray()) {
                System.out.println("🔎 Source identified: AbuseIPDB. Processing...");
                processAbuseIPDB(rootNode.path("data"));
            }
            // 2. Check if it's an AlienVault payload (they usually have a "results" array)
            else if (rootNode.has("results") && rootNode.path("results").isArray()) {
                System.out.println("🔎 Source identified: AlienVault OTX. Processing...");
                processAlienVault(rootNode.path("results"));
            }
            else {
                System.out.println("⚠️ Unknown payload structure. Ignoring message.");
            }

        } catch (Exception e) {
            System.err.println("❌ Error parsing JSON: " + e.getMessage());
        }
    }

    private void processAbuseIPDB(JsonNode dataArray) {
        int savedCount = 0;
        for (JsonNode node : dataArray) {
            String ipAddress = node.path("ipAddress").asText();
            saveThreatWithRanking(ipAddress, "IP", "AbuseIPDB");
            savedCount++;
            // Limit to 50 for testing to avoid console spam
            if (savedCount >= 50) break;
        }
        System.out.println("✅ Processed " + savedCount + " indicators from AbuseIPDB.");
    }

    private void processAlienVault(JsonNode resultsArray) {
        int savedCount = 0;
        for (JsonNode node : resultsArray) {
            // AlienVault structure is complex. We grab the 'name' which is often the indicator.
            String indicator = node.path("name").asText();
            if (indicator != null && !indicator.isEmpty()) {
                // Assume it's an IP or Domain for simplicity in this demo
                saveThreatWithRanking(indicator, "Indicator", "AlienVault OTX");
                savedCount++;
            }
            // Limit to 50 for testing
            if (savedCount >= 50) break;
        }
        System.out.println("✅ Processed " + savedCount + " indicators from AlienVault.");
    }

    // This method handles the orchestration: Extraction -> Ranking -> Database
    private void saveThreatWithRanking(String indicatorValue, String type, String source) {

        // 1. Action Item #4: Basic Data Validation (Don't save nulls)
        if (indicatorValue == null || indicatorValue.trim().isEmpty()) {
            return;
        }

        // 2. Action Item #2: IOC Enrichment via Ranking API
        Integer enrichedScore = rankingService.getEnrichedSeverityScore(indicatorValue);

        // 3. Prepare and Save
        Ioc threatInfo = new Ioc();
        threatInfo.setIocValue(indicatorValue);
        threatInfo.setIocType(type);
        threatInfo.setSeverityScore(enrichedScore);
        threatInfo.setSource(source);

        iocRepository.save(threatInfo);
    }
}