package com.eye.repository;

import com.eye.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByUsername(String username);
    Optional<Student> findByUsernameAndName(String username, String name);
    boolean existsByUsernameAndName(String username, String name);
    List<Student> findByUsernameAndFaceImage(String username, String faceImage);
}
