package com.eye.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "attendance_records")
public class AttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String studentName;

    @Column(nullable = false)
    private Long timestamp;

    @Column(name = "session_id", length = 64)
    private String sessionId;

    @Column(length = 50)
    private String username;

    @Column(name = "is_proxy")
    private Boolean isProxy = false;

    @Column(length = 255)
    private String note;

    @Column(name = "class_name", length = 50)
    private String className;

    public AttendanceRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Boolean getIsProxy() { return isProxy; }
    public void setIsProxy(Boolean isProxy) { this.isProxy = isProxy; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
