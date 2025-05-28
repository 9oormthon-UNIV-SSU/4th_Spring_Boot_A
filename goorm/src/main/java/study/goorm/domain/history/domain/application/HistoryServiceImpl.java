package study.goorm.domain.history.domain.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.goorm.domain.history.domain.converter.HistoryConverter;
import study.goorm.domain.history.domain.dto.HistoryResponseDTO;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.history.domain.exception.HistoryException;
import study.goorm.domain.history.domain.repository.HistoryImageRepository;
import study.goorm.domain.history.domain.repository.HistoryRepository;
import study.goorm.domain.member.domain.application.MemberService;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.member.domain.exception.MemberException;
import study.goorm.global.error.code.status.ErrorStatus;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
    private final HistoryImageRepository historyImageRepository;
    private final HistoryConverter historyConverter;
    private final MemberService memberService;


    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.MonthlyHistoriesResult getMonthlyHistories(String clokeyId, YearMonth month){

        Member member = (clokeyId == null)
                ? memberService.getCurrentMember()
                : memberService.findByClokeyId(clokeyId)
                .orElseThrow(() -> new MemberException(ErrorStatus.NO_SUCH_MEMBER));

        List<History> histories = historyRepository.findByMemberIdAndMonth(member.getId(), month);

        // 이미지 로직 따로 빼는게 더 나을까요? 아니면 그냥 두는게 나을까요
        List<HistoryResponseDTO.MonthlyHistoriesResult.HistoryDto> historiesDtos = histories.stream()
                .map(history -> {
                    String imageUrl = historyImageRepository
                            .findFirstByHistoryIdOrderByCreatedAtAsc(history.getId())
                            .map(HistoryImage::getImageUrl)
                            .orElseThrow(() -> new HistoryException(ErrorStatus.NO_CLOTH_IMAGE));
                    return historyConverter.toMonthlyHistoryDto(history, imageUrl);
                })
                .collect(Collectors.toList());

        return historyConverter.toMonthlyHistoriesResult(member, historiesDtos);
    }
}
