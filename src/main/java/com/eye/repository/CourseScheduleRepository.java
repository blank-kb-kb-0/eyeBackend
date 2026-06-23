package com.eye.repository;

import com.eye.entity.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long> {
    List<CourseSchedule> findByUsernameOrderByDayOfWeekAscStartTimeAsc(String username);
    void deleteByUsername(String username);
}
