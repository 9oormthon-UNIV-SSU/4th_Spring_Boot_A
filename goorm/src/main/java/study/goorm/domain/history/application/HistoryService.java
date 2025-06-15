package study.goorm.domain.history.application;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.history.dto.HistoryRequestDTO;
import study.goorm.domain.history.dto.HistoryResponseDTO;

import java.util.List;

public interface HistoryService {

    HistoryResponseDTO.MonthlyHistoryResult getMonthlyHistory(String clokeyId, String month);

    HistoryResponseDTO.DailyHistoryResult getDailyHistory(Long historyId);

    HistoryResponseDTO.HistoryCreateResult createHistory(HistoryRequestDTO.HistoryCreateRequest historyCreateRequest, List<MultipartFile> imageFiles);

    void patchHistory(HistoryRequestDTO.HistoryCreateRequest historyPatchRequest, List<MultipartFile> imageFiles, Long historyId);

    void deleteHistory(Long historyId);

}
