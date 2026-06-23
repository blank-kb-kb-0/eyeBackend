package com.eye.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "detection_records")
public class DetectionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_id", nullable = false, unique = true, length = 64)
    private String recordId;

    @Column(nullable = false)
    private Long timestamp;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String photo;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String results;

    @Column(name = "avg_confidence")
    private Double avgConfidence;

    @Column(length = 50)
    private String username;

    public DetectionRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public String getResults() { return results; }
    public void setResults(String results) { this.results = results; }
    public Double getAvgConfidence() { return avgConfidence; }
    public void setAvgConfidence(Double avgConfidence) { this.avgConfidence = avgConfidence; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}