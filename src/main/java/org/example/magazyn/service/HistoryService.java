package org.example.magazyn.service;

import org.example.magazyn.entity.History;
import org.example.magazyn.entity.User;

import java.util.List;

public interface HistoryService {
    History addHistoryEntry(Long userId, String operation, String details);
    List<History> getAllHistory();
    List<History> getUserHistory(Long userId);
}