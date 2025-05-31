package study.goorm.domain.history.application;

import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.history.dto.HistoryRequestDTO;
import study.goorm.domain.history.dto.HistoryResponseDTO;

public interface HistoryService {

    HistoryResponseDTO.HistoryMonthResult getMonthlyHistories(String clokeyId, String month);

    HistoryResponseDTO.HistoryDayResult getDailyHistory(Long historyId);

    Long createHistory(String clokeyId, HistoryRequestDTO.HistoryCreateDTO request, MultipartFile imageFile);

    void updateHistory(Long historyId, String clokeyId, HistoryRequestDTO.HistoryUpdateDTO request, MultipartFile imageFile);

    void deleteHistory(Long historyId, String clokeyId);

}
