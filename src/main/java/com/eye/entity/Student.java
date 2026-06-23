package com.eye.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String faceImage;

    @Column(length = 50)
    private String username;

    @Column(name = "student_id", length = 20, nullable = false, unique = true)
    private String studentId;

    @Column(name = "class_name", length = 50)
    private String className;

    public Student() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFaceImage() { return faceImage; }
    public void setFaceImage(String faceImage) { this.faceImage = faceImage; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
