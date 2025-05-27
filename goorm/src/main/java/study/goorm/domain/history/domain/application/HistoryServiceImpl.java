package study.goorm.domain.history.domain.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.goorm.domain.history.domain.dto.HistoryResponseDTO;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.exception.HistoryException;
import study.goorm.domain.history.domain.repository.HistoryRepository;
import study.goorm.global.error.code.status.ErrorStatus;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;

    @Override
    public HistoryResponseDTO.MonthlyHistoriesResult getMonthlyHistories(String clokeyId, LocalDateTime month){

        History history = historyRepository.findByClokeyId(clokeyId)
                .orElseThrow(()-> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));

        return null;
    }
}
