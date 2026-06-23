package com.eye.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "favorites")
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_id", nullable = false, length = 64)
    private String recordId;

    @Column(length = 50)
    private String username;

    public Favorite() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}