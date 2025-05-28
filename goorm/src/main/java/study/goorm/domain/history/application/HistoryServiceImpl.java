package study.goorm.domain.history.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.cloth.domain.repository.ClothRepository;
import study.goorm.domain.cloth.exception.ClothException;
import study.goorm.domain.history.converter.HistoryConverter;
import study.goorm.domain.history.domain.entity.*;
import study.goorm.domain.history.domain.repository.*;
import study.goorm.domain.history.dto.HistoryRequestDTO;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.history.exception.HistoryExeption;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.member.domain.exception.MemberException;
import study.goorm.domain.member.domain.repository.MemberRepository;
import study.goorm.global.error.code.status.ErrorStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
    private final HistoryImageQueryService historyImageQueryService;
    private final HistoryImageRepository historyImageRepository;
    private final MemberRepository memberRepository;
    private final HashtagHistoryRepository hashtagHistoryRepository;
    private final HistoryClothRepository historyClothRepository;
    private final ClothRepository clothRepository;
    private final HashtagRepository hashtagRepository;


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

        Map<Long, String> firstImagesOfHistory = historyImageQueryService.getFirstHistoryImageUrlMap(histories);

        return HistoryConverter.toHistoryGetMonthly(member, month, histories, historyImages);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.HistoryGetDaily getHistoryGetDaily(Long historyId) {
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryExeption(ErrorStatus.NO_SUCH_HISTORY));
        Member member = history.getMember();
        List<HistoryImage> images = historyImageRepository.findAllByHistory(history);
        List<HashtagHistory> hashtagHistories = hashtagHistoryRepository.findAllByHistory(history);
        List<String> hashtags = hashtagHistories.stream()
                .map(h -> "#" + h.getHashtag().getName())
                .toList();
        List<HistoryCloth> historyCloths = historyClothRepository.findAllByHistory(history);
        List<Cloth> cloths = historyCloths.stream()
                .map(HistoryCloth::getCloth)
                .toList();

        return HistoryConverter.toHistoryGetDaily(history,images, member, hashtags, cloths);
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
    @Transactional
    public HistoryResponseDTO.HistoryCreateResult createHistory(HistoryRequestDTO.HistoryCreateRequest historyCreateResult, MultipartFile image) {

        List<Cloth> clothes = clothRepository.findAllById(historyCreateResult.getClothes());
        if (clothes.isEmpty()) {
            throw new ClothException(ErrorStatus.NO_SUCH_CLOTH);
        }

        History newHistory = History.builder()
                .historyDate(LocalDate.parse(historyCreateResult.getDate()))
                .content(historyCreateResult.getContent())
                .build();
        historyRepository.save(newHistory);

        HistoryImage newImage = HistoryImage.builder()
                .history(newHistory)
                .imageUrl("url")
                .build();
        historyImageRepository.save(newImage);

        for (Cloth c : clothes) {
            historyClothRepository.findByHistoryAndCloth(newHistory, c);
        }

        List<String> tags = historyCreateResult.getHashtags();

        for (String name : tags) {
            Hashtag hashtag = hashtagRepository.findByName(name)
                    .orElseGet(() ->
                            hashtagRepository.save(
                                    Hashtag.builder()
                                            .name(name)
                                            .build()
                            )
                    );

            HashtagHistory mapping = HashtagHistory.builder()
                    .history(newHistory)
                    .hashtag(hashtag)
                    .build();

            hashtagHistoryRepository.save(mapping);
        }

        return HistoryConverter.toHistoryCreateResult(newHistory);
    }

    @Override
    @Transactional
    public void updateHistory(Long historyId, HistoryRequestDTO.HistoryCreateRequest historyUpdateRequest, MultipartFile image) {
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryExeption(ErrorStatus.NO_SUCH_HISTORY));

        // 기존 히스토리 정보 갱신
        history.setContent(historyUpdateRequest.getContent());
        history.setHistoryDate(LocalDate.parse(historyUpdateRequest.getDate()));

        // 기존 이미지 삭제 및 새 이미지 저장 (간단화된 예)
        historyImageRepository.deleteByHistory(history);

        HistoryImage newImage = HistoryImage.builder()
                .history(history)
                .imageUrl("newImageUrl")  // 실제 구현에서는 image 저장 처리 필요
                .build();
        historyImageRepository.save(newImage);

        // 기존 연결된 옷 정보 갱신
        historyClothRepository.deleteByHistory(history);
        List<Cloth> clothes = clothRepository.findAllById(historyUpdateRequest.getClothes());
        if (clothes.isEmpty()) {
            throw new ClothException(ErrorStatus.NO_SUCH_CLOTH);
        }

        for (Cloth cloth : clothes) {
            historyClothRepository.findByHistoryAndCloth(history, cloth); // 커스텀 메서드 필요
        }

        // 기존 해시태그 매핑 제거
        hashtagHistoryRepository.deleteByHistory(history);

        // 새 해시태그 등록
        List<String> tags = historyUpdateRequest.getHashtags();
        for (String name : tags) {
            Hashtag hashtag = hashtagRepository.findByName(name)
                    .orElseGet(() ->
                            hashtagRepository.save(
                                    Hashtag.builder()
                                            .name(name)
                                            .build()
                            )
                    );

            HashtagHistory mapping = HashtagHistory.builder()
                    .history(history)
                    .hashtag(hashtag)
                    .build();

            hashtagHistoryRepository.save(mapping);
        }
    }
}
