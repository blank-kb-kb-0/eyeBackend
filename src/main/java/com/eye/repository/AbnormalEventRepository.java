package com.eye.repository;

import com.eye.entity.AbnormalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AbnormalEventRepository extends JpaRepository<AbnormalEvent, Long> {
    List<AbnormalEvent> findByUsernameOrderByTimestampDesc(String username);
    List<AbnormalEvent> findTop5ByUsernameOrderByTimestampDesc(String username);
    void deleteByUsernameAndEventId(String username, String eventId);
}