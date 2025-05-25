package study.goorm.domain.history.application;

import study.goorm.domain.history.dto.HistoryResponseDTO;

public interface HistoryService {

    HistoryResponseDTO.MonthlyHistoryResult getMonthlyHistory(String clokeyId, String month);

    HistoryResponseDTO.MonthlyHistoryResult getDailyHistory(String HistoryId);
    void deleteHistory(Long historyId);
}
