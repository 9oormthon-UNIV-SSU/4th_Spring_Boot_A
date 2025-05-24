package study.goorm.domain.history.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.goorm.domain.history.converter.HistoryConverter;
import study.goorm.domain.cloth.exception.ClothException;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.history.domain.repository.HashtagHistoryRepository;
import study.goorm.domain.history.domain.repository.HistoryClothRepository;
import study.goorm.domain.history.domain.repository.HistoryImageRepository;
import study.goorm.domain.history.domain.repository.HistoryRepository;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.member.domain.repository.MemberRepository;
import study.goorm.global.error.code.status.ErrorStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService{

    private final HistoryRepository historyRepository;
    private final HistoryImageRepository historyImageRepository;
    private final HashtagHistoryRepository hashtagHistoryRepository;
    private final HistoryClothRepository historyClothRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.HistoryMonthResult getMonthlyHistories(String clokeyId, String month) {

        Member member = memberRepository.findByClokeyId(clokeyId)
                .orElseThrow(()-> new ClothException(ErrorStatus.NO_SUCH_MEMBER));

        // HistoryImage와 그에 연결된 History, Member를 함께 로드합니다.
        List<HistoryImage> fetchedHistoryImages = historyImageRepository.findMonthlyHistoryImagesWithDetails(clokeyId, month);

        // 모든 변환 로직을 컨버터의 단일 메소드에 위임합니다.
        return HistoryConverter.toHistoryMonthResult(member, fetchedHistoryImages);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.HistoryDayResult getDailyHistory(Long historyId) {

        // 1. History와 Member 정보 조회
        History history = historyRepository.findHistoryAndMemberById(historyId)
                .orElseThrow(() -> new ClothException(ErrorStatus.HISTORY_NOT_FOUND));

        Member authorMember = history.getMember();

        // 2. HistoryImage 목록 조회
        List<String> imageUrls = historyImageRepository.findByHistoryId(historyId).stream()
                .map(HistoryImage::getImageUrl)
                .collect(Collectors.toList());

        // 3. Hashtag 목록 조회
        List<String> hashtags = hashtagHistoryRepository.findByHistoryIdWithHashtag(historyId).stream()
                .map(hh -> hh.getHashtag().getName())
                .collect(Collectors.toList());

        // 4. Cloth 목록 조회
        List<HistoryResponseDTO.HistoryDayResult.ClothDTO> clothDTOs =
                historyClothRepository.findByHistoryIdWithCloth(historyId).stream()
                        .map(hc -> HistoryResponseDTO.HistoryDayResult.ClothDTO.builder() // DTO 클래스명 변경
                                .clothId(hc.getCloth().getId())
                                .clothImageUrl(hc.getCloth().getClothUrl())
                                .clothName(hc.getCloth().getName())
                                .build())
                        .collect(Collectors.toList());

        // 5. 댓글 수 조회
        Long commentCount = historyRepository.countCommentsByHistoryId(historyId);

        // 6. 좋아요 수 조회
        Long likeCount = historyRepository.countLikesByHistoryId(historyId);

        // 7. 현재 요청한 유저가 해당 기록에 좋아요를 눌렀는지 여부 확인
        boolean liked = false;
        /*
        if (viewerClokeyId != null) {
            Member viewerMember = memberRepository.findByClokeyId(viewerClokeyId)
                    .orElse(null);
            if (viewerMember != null) {
                liked = historyRepository.existsMemberLikeByHistoryIdAndMemberId(historyId, viewerMember.getId());
            }
        }
         */

        // Converter를 사용하여 DTO로 변환
        return HistoryConverter.toHistoryDayResult( // Converter 메소드명 변경 (아래에서 정의)
                history,
                authorMember,
                imageUrls,
                hashtags,
                clothDTOs,
                commentCount.intValue(),
                likeCount.intValue(),
                liked
        );
    }
}