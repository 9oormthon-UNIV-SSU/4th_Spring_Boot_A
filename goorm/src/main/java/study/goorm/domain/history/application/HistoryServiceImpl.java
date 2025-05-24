package study.goorm.domain.history.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.goorm.domain.history.converter.HistoryConverter;
import study.goorm.domain.cloth.exception.ClothException;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.history.domain.repository.HistoryImageRepository;
import study.goorm.domain.history.domain.repository.HistoryRepository;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.member.domain.repository.MemberRepository;
import study.goorm.global.error.code.status.ErrorStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService{

    private final HistoryImageRepository historyImageRepository;
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
}