package study.goorm.domain.history.application;

import study.goorm.domain.history.dto.HistoryResponseDTO;

public interface HistoryService {

    HistoryResponseDTO.HistoryMonthResult getMonthlyHistories(String clokeyId, String month);

    HistoryResponseDTO.HistoryDayResult getDailyHistory(Long historyId);

}
