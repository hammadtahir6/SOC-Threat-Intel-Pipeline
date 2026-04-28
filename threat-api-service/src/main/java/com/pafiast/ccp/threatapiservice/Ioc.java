package com.pafiast.ccp.threatapiservice;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "iocs")
public class Ioc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ioc_value")
    private String iocValue;

    @Column(name = "ioc_type")
    private String iocType;

    @Column(name = "severity_score")
    private Integer severityScore;

    private String source;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // --- Setters (To write data) ---
    public void setIocValue(String iocValue) { this.iocValue = iocValue; }
    public void setIocType(String iocType) { this.iocType = iocType; }
    public void setSeverityScore(Integer severityScore) { this.severityScore = severityScore; }
    public void setSource(String source) { this.source = source; }

    // --- Getters (So Spring Boot can read and print the JSON!) ---
    public Long getId() { return id; }
    public String getIocValue() { return iocValue; }
    public String getIocType() { return iocType; }
    public Integer getSeverityScore() { return severityScore; }
    public String getSource() { return source; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}