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

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final CommentRepository commentRepository;


    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.HistoryGetMonthly getHistoryGetMonthly(String clokeyId, String month) {
        // 사용자 정보 => 변경 사항
        Member member;
        // clokeyId가 null인 경우
        if (clokeyId == null) {
            member = memberRepository.findByClokeyId("1")
                    .orElseThrow(()-> new MemberException(ErrorStatus.NO_SUCH_MEMBER));
        } else { // clokeyId가 있는 경우
            member = memberRepository.findByClokeyId(clokeyId)
                    .orElseThrow(()-> new MemberException(ErrorStatus.NO_SUCH_MEMBER));
        }

        // Month 형식 검사
        try {
            YearMonth.parse(month);
        } catch (DateTimeException e) {
            e.printStackTrace();
        }

        // 기록 조회
        List<History> histories = historyRepository.findHistoriesByMemberIdAndYearMonth(member.getId(), month);

        List<Long> historyIds = histories.stream()
                .map(History::getId)
//                .toList();
                .collect(Collectors.toList()); // => toList와의 차이점은?

        // 사진 조회
        List<HistoryImage> historyImages = historyImageRepository.findAllByHistoryIdIn(historyIds);

        // 첫번째 이미지
        Map<Long, String> firstImagesOfHistory = historyImages.stream()
                .collect(Collectors.groupingBy(
                        img -> img.getHistory().getId(),
                        Collectors.mapping(HistoryImage::getImageUrl, Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.isEmpty() ? "private" : list.get(0)
                        ))
                ));

        List<HistoryResponseDTO.HistoryGetMonthlyResult> resultList = histories.stream()
                .map(history -> HistoryResponseDTO.HistoryGetMonthlyResult.builder()
                        .historyId(history.getId())
                        .date(LocalDate.parse(history.getHistoryDate().toString()))
                        .imageUrl(firstImagesOfHistory.getOrDefault(history.getId(), "비공개입니다"))
                        .build())
                .collect(Collectors.toList());

        return HistoryConverter.toHistoryGetMonthly(member, resultList);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.HistoryGetDaily getHistoryGetDaily(Long historyId) {
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryExeption(ErrorStatus.NO_SUCH_HISTORY));

        Member member = history.getMember();

        // 이미지 조회
        List<HistoryImage> historyImages = historyImageRepository.findAllByHistory(history);
        List<String> images = historyImages.stream()
                .map(HistoryImage::getImageUrl)
                .toList();

        // 해시태그 조회
        List<HashtagHistory> hashtagHistories = hashtagHistoryRepository.findByHistoryId(historyId);

        // 기록의 해시태그 조회
        List<Hashtag> hashtags = hashtagHistories.stream()
                .map(HashtagHistory::getHashtag)
                .toList();
        List<String> hashTagName = hashtags.stream()
                .map(Hashtag::getName)
                .toList();

        // 댓글 갯수 세기 => 이건 생각 못함
        long commentCount = commentRepository.countByHistoryId(historyId);

        List<Cloth> cloths =  clothRepository.findByMemberId(member.getId());

        List<HistoryResponseDTO.HistoryGetDailyCloth> clothList = cloths.stream()
                .map(cloth -> HistoryResponseDTO.HistoryGetDailyCloth.builder()
                        .clothId(cloth.getId())
                        .clothImageUrl(cloth.getClothUrl())  // 이미지 필드 맞게 수정
                        .clothName(cloth.getName())
                        .build())
                .toList();

        return HistoryConverter.toHistoryGetDaily(history, images, member, hashTagName, clothList);
    }

    @Override
    @Transactional
    public void deleteHistory(Long historyId) {
        History history = historyRepository.findById(historyId)
                .orElseThrow(()-> new HistoryExeption(ErrorStatus.NO_SUCH_HISTORY));

        //매핑 테이블 삭제
        hashtagHistoryRepository.deleteByHistory(history); // 해시태그 삭제
        historyImageRepository.deleteAllByHistory(history); // 이미지 삭제
        historyClothRepository.deleteByHistory(history); // 옷 매핑 삭제

        //최종 옷 삭제
        historyRepository.delete(history);
    }

    @Override
    @Transactional
    public HistoryResponseDTO.HistoryCreateResult createHistory(HistoryRequestDTO.HistoryCreateRequest historyCreateResult, List<MultipartFile> image) {
        // 이미지 업로드 개수 제한
        if (image.size() >= 10) {
            throw new HistoryExeption(ErrorStatus.TOO_MANY_IMAGES);
        }

        // member 1번이 로그인 한 유저라고 가정
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new HistoryExeption(ErrorStatus.NO_SUCH_MEMBER));

        // history 테이블에 내용, 날짜 저장
        History history = History.builder()
                .member(member)
                .content(historyCreateResult.getContent())
                .historyDate(historyCreateResult.getDate())
                .build();

        historyRepository.save(history);

        // clothId 검증
        List<Long> requestedIds = historyCreateResult.getClothes();

        for (Long id : requestedIds) {
            boolean exists = clothRepository.existsById(id);
            if (!exists) {
                throw new HistoryExeption(ErrorStatus.NO_SUCH_CLOTH);
            }
        }

        // ClothId 기록
        List<Cloth> clothes = clothRepository.findAllById(requestedIds);

        List<HistoryCloth> historyClothes = clothes.stream()
                .map(cloth -> HistoryCloth.builder()
                        .history(history)
                        .cloth(cloth)
                        .build())
                .toList();

        historyClothRepository.saveAll(historyClothes);

        List<String> requestedTags = historyCreateResult.getHashtags();

        // DB에 존재하는 해시태그 조회
        List<Hashtag> existingHashtags = hashtagRepository.findAllByNameIn(requestedTags);
        Set<String> existingTagNames = existingHashtags.stream()
                .map(Hashtag::getName)
                .collect(Collectors.toSet());

        // 없는 해시태그 추출
        List<Hashtag> newHashtags = requestedTags.stream()
                .filter(tag -> !existingTagNames.contains(tag))
                .map(tag -> Hashtag.builder().name(tag).build())
                .toList();

        // 새 해시태그 저장
        hashtagRepository.saveAll(newHashtags);

        // 기존 + 신규 해시태그 합치기
        List<Hashtag> allHashtags = new ArrayList<>();
        allHashtags.addAll(existingHashtags);
        allHashtags.addAll(newHashtags);

        // HashtagHistory 저장
        List<HashtagHistory> hashtagHistories = allHashtags.stream()
                .map(tag -> HashtagHistory.builder()
                        .hashtag(tag)
                        .history(history)
                        .build())
                .toList();
        hashtagHistoryRepository.saveAll(hashtagHistories);

        return HistoryConverter.toHistoryCreateResult(history);
    }

    @Override
    @Transactional
    public HistoryResponseDTO.HistoryUpdateResult updateHistory(HistoryRequestDTO.HistoryUpdateRequest historyUpdateRequest, List<MultipartFile> images, Long historyId) {
        if (images.size() >= 10) {
            throw new HistoryExeption(ErrorStatus.TOO_MANY_IMAGES);
        }

        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new HistoryExeption(ErrorStatus.NO_SUCH_MEMBER));

        // 기존 히스토리 조회
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryExeption(ErrorStatus.NO_SUCH_HISTORY));

        // content & visibility 수정
        history.update(historyUpdateRequest.getContent());

        // 기존 clothes & hashtag 관계 삭제
        historyClothRepository.deleteByHistory(history); // custom deleteByHistory
        hashtagHistoryRepository.deleteByHistory(history); // custom deleteByHistory

        // 새로운 clothes 저장
        List<Long> clothIds = historyUpdateRequest.getClothes();
        List<Cloth> clothes = clothRepository.findAllById(clothIds);

        List<HistoryCloth> historyClothes = clothes.stream()
                .map(cloth -> HistoryCloth.builder()
                        .history(history)
                        .cloth(cloth)
                        .build())
                .toList();
        historyClothRepository.saveAll(historyClothes);

        // 새로운 해시태그 저장 (새로운 해시태그 생성 포함)
        List<String> tagNames = historyUpdateRequest.getHashtags();

        List<Hashtag> existingTags = hashtagRepository.findAllByNameIn(tagNames);
        Set<String> existingTagNames = existingTags.stream()
                .map(Hashtag::getName)
                .collect(Collectors.toSet());

        List<Hashtag> newTags = tagNames.stream()
                .filter(name -> !existingTagNames.contains(name))
                .map(name -> Hashtag.builder().name(name).build())
                .toList();

        hashtagRepository.saveAll(newTags);

        List<Hashtag> allTags = new ArrayList<>();
        allTags.addAll(existingTags);
        allTags.addAll(newTags);

        List<HashtagHistory> hashtagHistories = allTags.stream()
                .map(tag -> HashtagHistory.builder()
                        .hashtag(tag)
                        .history(history)
                        .build())
                .toList();

        hashtagHistoryRepository.saveAll(hashtagHistories);

        // 기존 이미지 삭제
        List<HistoryImage> existingImages = historyImageRepository.findAllByHistory(history);


        // DB에서 HistoryImage 삭제
        historyImageRepository.deleteAllByHistory(history);


        return HistoryConverter.toHistoryUpdateResult(history);
    }
}
