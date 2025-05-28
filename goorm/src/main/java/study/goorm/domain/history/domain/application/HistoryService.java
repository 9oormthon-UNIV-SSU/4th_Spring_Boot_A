package study.goorm.domain.history.domain.application;

import study.goorm.domain.history.domain.dto.HistoryResponseDTO;

import java.time.LocalDateTime;
import java.time.YearMonth;

public interface HistoryService {
    HistoryResponseDTO.MonthlyHistoriesResult getMonthlyHistories(String clokeyId, YearMonth month);
}
