package org.example.magazyn.repository;

import org.example.magazyn.entity.History;
import org.example.magazyn.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByUserId(Long userId);
    List<History> findAllByOrderByTimestampDesc();
}