package study.goorm.domain.history.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.repository.*;
import study.goorm.domain.history.exception.HistoryException;
import study.goorm.global.error.code.status.ErrorStatus;


@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService{
    private final HistoryRepository historyRepository;
    private final CommentRepository commentRepository;
    private final HashtagHistoryRepository hashtagHistoryRepository;
    private final HistoryClothRepository historyClothRepository;
    private final HistoryImageRepository historyImageRepository;
    private final MemberLikeRepository memberLikeRepository;



    @Override
    @Transactional
    public void deleteHistory(Long historyId) {
        History history = historyRepository.findById(historyId)
                .orElseThrow(()-> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));

        // 매핑 테이블 삭제
        commentRepository.deleteAllByHistory(history);
        hashtagHistoryRepository.deleteAllByHistory(history);
        historyClothRepository.deleteAllByHistory(history);
        historyImageRepository.deleteAllByHistory(history);
        memberLikeRepository.deleteAllByHistory(history);

        // 최종 기록 삭제
        historyRepository.delete(history);
    }
}