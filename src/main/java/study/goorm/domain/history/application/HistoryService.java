package study.goorm.domain.history.application;

import study.goorm.domain.history.dto.HistoryResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface HistoryService {

    HistoryResponseDTO.MonthlyHistoryPreview getMonthlyPreview(String clokeyId, String date);

    HistoryResponseDTO.DailyHistoryPreview getDailyPreview(Long historyId);
}
