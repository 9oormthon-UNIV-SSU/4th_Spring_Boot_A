package study.goorm.domain.history.application;

import study.goorm.domain.history.dto.HistoryResponseDTO;

public interface HistoryService {

    HistoryResponseDTO.MontlyHistoryResult getMonthlyHistory(String clokeyId, String month);

    void deleteHistory(Long historyId);
}
