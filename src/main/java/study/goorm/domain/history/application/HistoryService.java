package study.goorm.domain.history.application;

import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.history.dto.HistoryRequestDTO;
import study.goorm.domain.history.dto.HistoryResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface HistoryService {

    HistoryResponseDTO.MonthlyHistoryPreview getMonthlyPreview(String clokeyId, String date);

    HistoryResponseDTO.DailyHistoryPreview getDailyPreview(Long historyId);

    HistoryResponseDTO.HistoryCreateResult createHistory(HistoryRequestDTO.HistoryCreateRequest historyCreateRequest, List<MultipartFile> imageFile);

    HistoryResponseDTO.HistoryUpdateResult updateHistory(HistoryRequestDTO.HistoryUpdateRequest historyUpdateRequest, List<MultipartFile> imageFile, Long historyId);
}
