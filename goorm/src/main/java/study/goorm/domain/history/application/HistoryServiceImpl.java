package study.goorm.domain.history.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.history.converter.HistoryConverter;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.history.domain.repository.HistoryImageRepository;
import study.goorm.domain.history.domain.repository.HistoryRepository;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.member.domain.exception.MemberException;
import study.goorm.domain.member.domain.repository.MemberRepository;
import study.goorm.domain.model.enums.ClothSort;
import study.goorm.global.error.code.status.ErrorStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
    private final HistoryImageRepository historyImageRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.HistoryGetMonthly getHistoryGetMonthly(String clokeyId, LocalDate month) {
        Member member = memberRepository.findByClokeyId(clokeyId)
                .orElseThrow(()-> new MemberException(ErrorStatus.NO_SUCH_MEMBER));

        // 해당 월의 시작일과 마지막일 계산
        LocalDate startOfMonth = month.withDayOfMonth(1);
        LocalDate endOfMonth = month.withDayOfMonth(month.lengthOfMonth());

        List<History> histories = historyRepository.findAllByMemberAndHistoryDateBetween(member, startOfMonth, endOfMonth);
        List<Long> historyIds = histories.stream()
                .map(History::getId)
                .toList();
        List<HistoryImage> historyImages = historyImageRepository.findAllByHistoryIdIn(historyIds);

        return HistoryConverter.toHistoryGetMonthly(member, month, histories, historyImages);
    }
}
