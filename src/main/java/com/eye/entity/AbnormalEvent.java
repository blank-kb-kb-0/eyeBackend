package com.eye.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "abnormal_events")
public class AbnormalEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, length = 64)
    private String eventId;

    @Column(nullable = false)
    private Long timestamp;

    @Column(name = "person_name", length = 50)
    private String personName;

    @Column(length = 50)
    private String behavior;

    @Column(name = "behavior_label", length = 100)
    private String behaviorLabel;

    @Column(length = 100)
    private String location;

    private Double confidence;

    @Column(name = "source_history_id", length = 64)
    private String sourceHistoryId;

    @Column(length = 50)
    private String username;

    public AbnormalEvent() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }
    public String getBehavior() { return behavior; }
    public void setBehavior(String behavior) { this.behavior = behavior; }
    public String getBehaviorLabel() { return behaviorLabel; }
    public void setBehaviorLabel(String behaviorLabel) { this.behaviorLabel = behaviorLabel; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public String getSourceHistoryId() { return sourceHistoryId; }
    public void setSourceHistoryId(String sourceHistoryId) { this.sourceHistoryId = sourceHistoryId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}