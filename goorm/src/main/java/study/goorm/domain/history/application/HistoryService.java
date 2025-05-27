package study.goorm.domain.history.application;

import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.history.dto.HistoryRequestDTO;
import study.goorm.domain.history.dto.HistoryResponseDTO;

import java.time.LocalDate;

public interface HistoryService {
    HistoryResponseDTO.HistoryGetMonthly getHistoryGetMonthly(String clokeyId, LocalDate month);
    void deleteHistory(Long historyId);
    HistoryResponseDTO.HistoryCreateResult createHistory(HistoryRequestDTO.HistoryCreateRequest historyCreateResult, MultipartFile image);
}
