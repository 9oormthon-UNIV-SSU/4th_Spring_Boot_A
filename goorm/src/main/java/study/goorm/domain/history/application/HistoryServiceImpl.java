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
import study.goorm.domain.member.domain.dto.LikedMemberDTO;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.member.domain.exception.MemberException;
import study.goorm.domain.member.domain.repository.MemberRepository;
import study.goorm.global.error.code.status.ErrorStatus;

import java.io.File;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
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
    private final MemberLikeRepository memberLikeRepository;


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
            throw new HistoryExeption(ErrorStatus.INVALID_DATE_FORMAT);
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
                                list -> list.isEmpty() ? "null" : list.get(0)
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

        //=> fetch join or jpql로 바로 가져오기
        // 기록의 해시태그 조회
        List<Hashtag> hashtags = hashtagHistories.stream()
                .map(HashtagHistory::getHashtag)
                .toList();
        List<String> hashTagName = hashtags.stream()
                .map(Hashtag::getName)
                .toList();

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
        // History 조회
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryExeption(ErrorStatus.NO_SUCH_HISTORY));

        // 댓글 전부 삭제
        commentRepository.deleteByHistory(history);

        // 좋아요 전부 삭제
        memberLikeRepository.deleteByHistory(history);

        // 옷 착용 횟수 감소
        List<HistoryCloth> historyClothes = historyClothRepository.findByHistory(history);
        for (HistoryCloth hc : historyClothes) {
            Cloth cloth = hc.getCloth();
            cloth.decreaseWearCount();
        }

        // HashtagHistory 삭제
        hashtagHistoryRepository.deleteByHistory(history);

        // 이미지 삭제
        historyImageRepository.deleteAllByHistory(history);

        // History-Cloth 매핑 row 삭제
        historyClothRepository.deleteByHistory(history);

        // 최종 History 삭제
        historyRepository.delete(history);
    }

    @Override
    @Transactional
    public HistoryResponseDTO.HistoryCreateResult createHistory(HistoryRequestDTO.HistoryCreateRequest historyCreateResult, List<MultipartFile> image) {
        // 이미지 업로드 개수 제한
        if (image.size() >= 10) {
            throw new HistoryExeption(ErrorStatus.TOO_MANY_IMAGES);
        }

        // 이미 그 날짜에 history 검증.

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

        // content 수정
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

        // DB에서 HistoryImage 삭제
        historyImageRepository.deleteAllByHistory(history);

        String uploadDir = "history/"; // 상대 경로 또는 절대 경로
        List<HistoryImage> newHistoryImages = new ArrayList<>();

        for (MultipartFile file : images) {
            if (file.isEmpty()) continue;

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueName = UUID.randomUUID().toString() + extension;

            File dest = new File(uploadDir + uniqueName);
            try {
                file.transferTo(dest); // 실제 파일 저장
            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 실패", e);
            }

            String imageUrl = "/uploads/history/" + uniqueName; // 클라이언트 접근 경로
            HistoryImage historyImage = HistoryImage.builder()
                    .history(history)
                    .imageUrl(imageUrl)
                    .build();

            newHistoryImages.add(historyImage);
        }

        historyImageRepository.saveAll(newHistoryImages);

        return HistoryConverter.toHistoryUpdateResult(history);
    }

    // 좋아요 기능
    @Override
    @Transactional
    public HistoryResponseDTO.HistoryLikeResult changeLikeStatus(Long memberId, Long historyId, boolean isLiked) {
        History history = historyRepository.findById(historyId)
                .orElseThrow(()-> new HistoryExeption(ErrorStatus.NO_SUCH_HISTORY));

        // 이미 게시물이 좋아요한 상태일 경우
        if (isLiked) {
            history.decreaseLikes();
            // 해당 게시물 좋아요 취소를 통해 멤버아이디와 기록아이디 삭제
            memberLikeRepository.deleteByMemberIdAndHistoryId(memberId, historyId);
        }
        else { // 게시물이 좋아요한 상태가 아닐 경우
            history.increaseLikes();
            // 해당 게시물 좋아요를 통해 멤버아이디와 기록아이디 등록
            MemberLike memberLike = MemberLike.builder()
                    .history(history)
                    .member(memberRepository.findMemberById(memberId))
                    .build();
            memberLikeRepository.save(memberLike);
        }

        return HistoryConverter.toHistoryLikeResult(history, isLiked);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.HistoryLikedUserResultList getLikedUsers(
            Long loginMemberId, Long historyId
    ) {
        List<LikedMemberDTO> likedMembers =
                memberRepository.findLikedMembersWithFollowInfo(historyId, loginMemberId);

        return HistoryConverter.toLikedUserResult(likedMembers);
    }
}
