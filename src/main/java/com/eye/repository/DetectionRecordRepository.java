package com.eye.repository;

import com.eye.entity.DetectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DetectionRecordRepository extends JpaRepository<DetectionRecord, Long> {
    List<DetectionRecord> findByUsernameOrderByTimestampDesc(String username);
    List<DetectionRecord> findByUsernameAndRecordId(String username, String recordId);
    void deleteByUsernameAndRecordId(String username, String recordId);
}