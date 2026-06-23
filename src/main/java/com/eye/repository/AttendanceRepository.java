package com.eye.repository;

import com.eye.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findByUsernameAndSessionIdOrderByTimestampDesc(String username, String sessionId);
    List<AttendanceRecord> findByUsernameOrderByTimestampDesc(String username);
    List<AttendanceRecord> findByUsernameAndStudentName(String username, String studentName);
    void deleteByUsernameAndSessionId(String username, String sessionId);
}
