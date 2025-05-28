package study.goorm.domain.history.domain.application;

import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.history.domain.dto.HistoryRequestDTO;
import study.goorm.domain.history.domain.dto.HistoryResponseDTO;

import java.time.YearMonth;
import java.util.List;

public interface HistoryService {
    HistoryResponseDTO.MonthlyHistoriesResult getMonthlyHistories(String clokeyId, YearMonth month);

    HistoryResponseDTO.DailyHistoryResult getDailyHistory(Long historyId);

    HistoryResponseDTO.HistoryCreateResult createHistory(HistoryRequestDTO.HistoryCreateRequest historyCreateRequest, List<MultipartFile> imageFiles);

    void updateHistory(Long historyId, HistoryRequestDTO.HistoryUpdateRequest request, List<MultipartFile> imageFiles);

    void deleteHistory(Long historyId);

}
