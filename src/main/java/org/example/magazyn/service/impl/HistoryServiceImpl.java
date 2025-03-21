package org.example.magazyn.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.magazyn.entity.History;
import org.example.magazyn.entity.User;
import org.example.magazyn.repository.HistoryRepository;
import org.example.magazyn.service.HistoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;

    @Override
    public History addHistoryEntry(Long userId, String operation, String details) {
        History history = new History();
        history.setUserId(userId);
        history.setOperation(operation);
        history.setTimestamp(LocalDateTime.now());
        history.setDetails(details);

        return historyRepository.save(history);
    }

    @Override
    public List<History> getAllHistory() {
        return historyRepository.findAllByOrderByTimestampDesc();
    }

    @Override
    public List<History> getUserHistory(Long userId) {
        return historyRepository.findByUserId(userId);
    }
}