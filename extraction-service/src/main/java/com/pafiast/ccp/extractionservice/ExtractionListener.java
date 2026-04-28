package com.pafiast.ccp.extractionservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ExtractionListener {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Bring in our database tool
    private final IocRepository iocRepository;

    public ExtractionListener(IocRepository iocRepository) {
        this.iocRepository = iocRepository;
    }

    @KafkaListener(topics = "raw-iocs", groupId = "extraction-group")
    public void consumeRawData(String message) {
        System.out.println("\n🔥 [Kafka Alert] Extracting and Saving to Database...");

        try {
            JsonNode rootNode = objectMapper.readTree(message);
            JsonNode dataArray = rootNode.path("data");

            if (dataArray.isArray()) {
                int savedCount = 0;

                for (JsonNode node : dataArray) {
                    // 1. Extract the data
                    String ipAddress = node.path("ipAddress").asText();
                    int score = node.path("abuseConfidenceScore").asInt(); // Extracting the built-in score!

                    // 2. Prepare the database object
                    Ioc threatInfo = new Ioc();
                    threatInfo.setIocValue(ipAddress);
                    threatInfo.setIocType("IP");
                    threatInfo.setSeverityScore(score);
                    threatInfo.setSource("AbuseIPDB Kafka Pipeline");

                    // 3. Save it to MySQL
                    iocRepository.save(threatInfo);
                    savedCount++;
                }

                System.out.println("✅ SUCCESSFULLY SAVED " + savedCount + " THREATS TO MYSQL DATABASE!");
                System.out.println("------------------------------------------------------------\n");
            }

        } catch (Exception e) {
            System.err.println("❌ Error processing data: " + e.getMessage());
        }
    }
}