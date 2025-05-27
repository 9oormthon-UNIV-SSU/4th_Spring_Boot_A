package study.goorm.domain.history.domain.application;

import study.goorm.domain.history.domain.dto.HistoryResponseDTO;

import java.time.LocalDateTime;

public interface HistoryService {
    HistoryResponseDTO.MonthlyHistoriesResult getMonthlyHistories(String clokeyId, LocalDateTime month);
}
