package com.pafiast.ccp.extractionservice;

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

    // --- Getters and Setters ---
    public void setIocValue(String iocValue) { this.iocValue = iocValue; }
    public void setIocType(String iocType) { this.iocType = iocType; }
    public void setSeverityScore(Integer severityScore) { this.severityScore = severityScore; }
    public void setSource(String source) { this.source = source; }
}