package study.goorm.domain.history.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.goorm.domain.history.converter.HistoryConverter;
import study.goorm.domain.history.domain.entity.Hashtag;
import study.goorm.domain.history.domain.entity.HashtagHistory;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.history.domain.repository.*;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.history.exception.HistoryException;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.member.domain.exception.MemberException;
import study.goorm.domain.member.domain.repository.MemberRepository;
import study.goorm.global.error.code.status.ErrorStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService{
    private final MemberRepository memberRepository;
    private final HistoryRepository historyRepository;
    private final CommentRepository commentRepository;
    private final HashtagHistoryRepository hashtagHistoryRepository;
    private final HistoryClothRepository historyClothRepository;
    private final HistoryImageRepository historyImageRepository;
    private final MemberLikeRepository memberLikeRepository;
    private final HistoryImageQueryService historyImageQueryService;


    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.MonthlyHistoryResult getMonthlyHistory(String clokeyId, String month) {

        Member member = memberRepository.findByClokeyId(clokeyId)
                .orElseThrow(()-> new MemberException(ErrorStatus.NO_SUCH_MEMBER));

        List<History> histories = historyRepository.findAllByMemberIdAndMonth(member.getId(), month);
        if (histories.isEmpty()) {
            throw new HistoryException(ErrorStatus.BAD_DATE_TYPE);
        }

        Map<Long, String> firstImagesOfHistory = historyImageQueryService.getFirstImageUrlMap(histories);

        return HistoryConverter.toMonthlyHistoryResult(member, firstImagesOfHistory, histories);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.DailyHistoryResult getDailyHistory(Long historyId) {
        History history = historyRepository.findById(historyId)
                .orElseThrow(()-> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));

        List<HistoryImage> historyImages = historyImageRepository.findAllByHistory(history);
        List<HashtagHistory> hashtagHistories = hashtagHistoryRepository.findByHistory(history);

// 2. 각 HashtagHistory에서 Hashtag만 추출
        List<Hashtag> hashtags = hashtagHistories.stream()
                .map(HashtagHistory::getHashtag)
                .collect(Collectors.toList());

        return null;
    }

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