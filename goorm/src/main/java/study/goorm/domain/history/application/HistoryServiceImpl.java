package study.goorm.domain.history.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.cloth.application.ClothImageQueryService;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.cloth.domain.repository.ClothImageRepository;
import study.goorm.domain.cloth.domain.repository.ClothRepository;
import study.goorm.domain.history.converter.HistoryConverter;
import study.goorm.domain.history.domain.entity.*;
import study.goorm.domain.history.domain.repository.*;
import study.goorm.domain.history.dto.HistoryRequestDTO;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.history.exception.HistoryException;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.member.domain.exception.MemberException;
import study.goorm.domain.member.domain.repository.MemberRepository;
import study.goorm.global.error.code.status.ErrorStatus;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.*;
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
    private final ClothImageRepository clothImageRepository;
    private final ClothImageQueryService clothImageQueryService;
    private final ClothRepository clothRepository;
    private final HashtagRepository hashtagRepository;


    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.MonthlyHistoryResult getMonthlyHistory(String clokeyId, String month) {
        // 날짜 형식 검증
        if(!(month != null && month.matches("^\\d{4}-(0[1-9]|1[0-2])$"))){
            throw new HistoryException(ErrorStatus.BAD_DATE_TYPE);
        }

        Member member;
        if (clokeyId == null) { // clokeyId가 null이면 "1"로 가정하고 구현
            clokeyId = "clo001";
        }
        member = memberRepository.findByClokeyId(clokeyId)
                .orElseThrow(()-> new MemberException(ErrorStatus.NO_SUCH_MEMBER));

        List<History> histories = historyRepository.findAllByMemberIdAndMonth(member.getId(), month);
        if (histories.isEmpty()) {
            throw new HistoryException(ErrorStatus.NO_SUCH_HISTORY);
        }

        Map<Long, String> firstImagesOfHistory = historyImageQueryService.getFirstImageUrlMap(histories);

        return HistoryConverter.toMonthlyHistoryResult(member, firstImagesOfHistory, histories);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.DailyHistoryResult getDailyHistory(Long historyId) {
        // history 불러옴
        History history = historyRepository.findById(historyId)
                .orElseThrow(()-> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));

        Member member = history.getMember();

        // 기록 사진 리스트
        List<HistoryImage> historyImages = historyImageRepository.findAllByHistory(history);
        if (historyImages.isEmpty()) {
            throw new HistoryException(ErrorStatus.NO_HISTORY_IMAGE);
        }
        //historyImages에서 ImageUrl만 추출
        List<String> historyImageUrls = historyImages.stream()
                .map(HistoryImage::getImageUrl)
                .collect(Collectors.toList());

        List<HashtagHistory> hashtagHistories = hashtagHistoryRepository.findByHistory(history);

        //  각 HashtagHistory에서 Hashtag만 추출
        List<Hashtag> hashtags = hashtagHistories.stream()
                .map(HashtagHistory::getHashtag)
                .collect(Collectors.toList());

        List<String> hashtagNames = hashtags.stream()
                .map(Hashtag::getName)
                .toList();

        // memberId = 1로 구현
        boolean liked = memberLikeRepository.existsByHistoryIdAndMemberId(historyId, 1L);

        List<HistoryCloth> historyClothes = historyClothRepository.findAllByHistory(history);
        // 각 historyClothes에서 clothes만 추출
        List<Cloth> clothes = historyClothes.stream()
                .map(HistoryCloth::getCloth)
                .collect(Collectors.toList());

        // 각 옷의 첫번째 사진만 가져옴
        Map<Long, String> firstImagesOfCloth = clothImageQueryService.getFirstImageUrlMap(clothes);

        return HistoryConverter.toDailyHistoryResult(history, member, liked, historyImageUrls, hashtagNames, clothes, firstImagesOfCloth);
    }

    @Override
    @Transactional
    public HistoryResponseDTO.HistoryCreateResult createHistory(HistoryRequestDTO.HistoryCreateRequest historyCreateRequest, List<MultipartFile> imageFiles) {

        // 이미지 업로드 개수 제한 <= 10
        if (imageFiles.size() >= 10) {
            throw new HistoryException(ErrorStatus.TOO_MANY_IMAGES);
        } else if (imageFiles.size() == 0) {
            throw new HistoryException(ErrorStatus.NO_IMAGE_SENT);
        }

        // 날짜 형식이 맞는지 검사
        try {
            LocalDate.parse(historyCreateRequest.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            throw new HistoryException(ErrorStatus.BAD_DATE_TYPE);
        }
        LocalDate date = LocalDate.parse(historyCreateRequest.getDate()); // LocalDate 형식으로 변환

        // member 1번이 로그인 한 유저라고 가정
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_MEMBER));

        // 옷이 중복되는지 검사 중복시 예외처리
        List<Long> clothesList = historyCreateRequest.getClothes();
        Set<Long> set = new HashSet<>(clothesList);
        if (clothesList.size() != set.size()) throw new HistoryException(ErrorStatus.CLOTHES_NOT_UNIQUE);

        // 해시태그가 중복되는지 검사
        List<String> hashtagsList = historyCreateRequest.getHashtags();
        Set<String> set2 = new HashSet<>(hashtagsList);
        if (hashtagsList.size() != set2.size()) throw new HistoryException(ErrorStatus.HASHTAGS_NOT_UNIQUE);

        // 각 옷의 WearNum + 1 씩 해줌
        // (트랜잭션(@Transactional)안에서 실행시  JPA가 변경 감지(Dirty Checking)해서 자동으로 DB에 반영
        List<Cloth> clothes = clothRepository.findAllById(clothesList);
        for(Cloth c : clothes) {
            c.setWearNum(c.getWearNum() + 1);
        }

        // history테이블에 저장
        History newHistory = History.builder()
                .content(historyCreateRequest.getContent())
                .historyDate(date)
                .likes(0)
                .member(member)
                .build();
        historyRepository.save(newHistory);

        // DB에 존재하는 해시태그 조회
        List<Hashtag> existingHashtags = hashtagRepository.findAllByNameIn((historyCreateRequest.getHashtags()));
        Set<String> existingTagNames = existingHashtags.stream()
                .map(Hashtag::getName)
                .collect(Collectors.toSet());

        // 없는 해시태그 추출
        List<Hashtag> newHashtags = historyCreateRequest.getHashtags().stream()
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
                        .history(newHistory)
                        .build())
                .toList();
        hashtagHistoryRepository.saveAll(hashtagHistories);

        return HistoryConverter.toHistoryCreateResult(newHistory);

    }

    @Override
    @Transactional
    public void patchHistory(HistoryRequestDTO.HistoryCreateRequest historyCreateRequest, List<MultipartFile> imageFiles, Long historyId){
        //나의 기록이 아니면 에러 (memberId 1이 사용자라고 가정)
        Long LoginMemberId = 1L;
        History history = historyRepository.findById(historyId)
                .orElseThrow(()-> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));
        if(!history.getMember().getId().equals(LoginMemberId)) {
            throw new HistoryException(ErrorStatus.HISTORY_PATCH_DENIED);
        }

        // 옷이 중복되는지 검사 중복시 예외처리
        List<Long> clothesList = historyCreateRequest.getClothes();
        Set<Long> set = new HashSet<>(clothesList);
        if (clothesList.size() != set.size()) throw new HistoryException(ErrorStatus.CLOTHES_NOT_UNIQUE);

        // 이미지 업로드 개수 제한 <= 10
        if (imageFiles.size() >= 10) {
            throw new HistoryException(ErrorStatus.TOO_MANY_IMAGES);
        } else if (imageFiles.isEmpty()) {
            throw new HistoryException(ErrorStatus.NO_IMAGE_SENT);
        }

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