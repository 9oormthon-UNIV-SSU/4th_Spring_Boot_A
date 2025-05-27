package study.goorm.domain.history.application;

import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.history.dto.HistoryRequestDTO;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.member.domain.entity.Member;

import java.time.LocalDate;

public interface HistoryService {
    HistoryResponseDTO.HistoryGetMonthly getHistoryGetMonthly(String clokeyId, LocalDate month);
    HistoryResponseDTO.HistoryGetDaily getHistoryGetDaily(Long historyId);
    void deleteHistory(Long historyId);
    HistoryResponseDTO.HistoryCreateResult createHistory(HistoryRequestDTO.HistoryCreateRequest historyCreateResult, MultipartFile image);
    HistoryResponseDTO.HistoryCreateResult updateHistory(Long historyId, HistoryRequestDTO.HistoryCreateRequest historyUpdateRequest, MultipartFile image);
}
