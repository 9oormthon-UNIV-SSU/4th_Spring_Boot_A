package study.goorm.domain.history.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.cloth.domain.repository.ClothRepository;
import study.goorm.domain.cloth.exception.ClothException;
import study.goorm.domain.history.converter.HistoryConverter;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryCloth;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.history.domain.repository.HashtagHistoryRepository;
import study.goorm.domain.history.domain.repository.HistoryClothRepository;
import study.goorm.domain.history.domain.repository.HistoryImageRepository;
import study.goorm.domain.history.domain.repository.HistoryRepository;
import study.goorm.domain.history.dto.HistoryRequestDTO;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.history.exception.HistoryExeption;
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
    private final HashtagHistoryRepository hashtagHistoryRepository;
    private final HistoryClothRepository historyClothRepository;
    private final ClothRepository clothRepository;


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

    @Override
    @Transactional
    public void deleteHistory(Long historyId) {
        History history = historyRepository.findById(historyId)
                .orElseThrow(()-> new HistoryExeption(ErrorStatus.NO_SUCH_HISTORY));

        //매핑 테이블 삭제
        hashtagHistoryRepository.deleteByHistory(history);
        historyImageRepository.deleteByHistory(history);
        historyClothRepository.deleteByHistory(history);

        //최종 옷 삭제
        historyRepository.delete(history);
    }

    @Override
    public HistoryResponseDTO.HistoryCreateResult createHistory(HistoryRequestDTO.HistoryCreateRequest historyCreateResult, MultipartFile image) {
        List<Cloth> cloth = clothRepository.findAllById(historyCreateResult.getClothes());
        if (cloth.isEmpty()) {
            throw new ClothException(ErrorStatus.NO_SUCH_CLOTH);
        }


        History newHistory = History.builder()
                .historyDate(LocalDate.parse(historyCreateResult.getDate()))
                .content(historyCreateResult.getContent())
                .build();
        HistoryImage newHistoryImage = HistoryImage.builder()
                .history(newHistory)
                .imageUrl("url")
                .build();

        historyImageRepository.save(newHistoryImage);


        return HistoryConverter.toHistoryCreateResult(newHistory);
    }
}
