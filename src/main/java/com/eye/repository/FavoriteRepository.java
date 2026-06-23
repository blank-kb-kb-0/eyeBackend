package com.eye.repository;

import com.eye.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUsername(String username);
    Optional<Favorite> findByUsernameAndRecordId(String username, String recordId);
    boolean existsByUsernameAndRecordId(String username, String recordId);
    void deleteByUsernameAndRecordId(String username, String recordId);
}