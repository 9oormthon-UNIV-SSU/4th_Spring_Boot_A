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
import study.goorm.storage.FileStorageService;

import java.io.IOException;
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
    private final FileStorageService fileStorageService;


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
    @Transactional(readOnly = true)
    public HistoryResponseDTO.LikedUsersResult getLikedUsers(Long historyId) {
        // history 불러옴
        History history = historyRepository.findById(historyId)
                .orElseThrow(()-> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));
        // memberId 1번이 로그인한 유저라고 가정
        Long loginMemberId = 1L;
        Member member = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_MEMBER));

        List<MemberLike> memberLikes = memberLikeRepository.findAllByHistory(history);
        List<Member> likedUsersList = memberLikes.stream()
                .map(MemberLike::getMember)
                .collect(Collectors.toList());

        return HistoryConverter.toLikedUsersResult(likedUsersList, member);
    }

    @Override
    @Transactional
    public HistoryResponseDTO.HistoryCreateResult createHistory(HistoryRequestDTO.HistoryCreateRequest historyCreateRequest, List<MultipartFile> imageFiles) {

        // 이미지 업로드 개수 제한 < 10, != 0
        validateImageCount(imageFiles);

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

        List<Long> requestedClothIds = historyCreateRequest.getClothes();

        // 해시태그가 중복되는지 검사
        List<String> hashtagsList = historyCreateRequest.getHashtags();
        validateHasgtagsDuplicatoin(hashtagsList);

        // 옷이 중복되는지 검사 중복시 예외처리
        validateClothesDuplication(requestedClothIds);

        // 옷이 사용자의 소유인지, 존재하는 옷인지 검증
        List<Cloth> ownedClothes = validateAndGetClothesOwnership(requestedClothIds, member);

        // 각 옷의 WearNum + 1 해줌
        increaseWearCounts(ownedClothes);

        // history테이블에 저장
        History newHistory = History.builder()
                .content(historyCreateRequest.getContent())
                .historyDate(date)
                .likes(0)
                .member(member)
                .build();
        historyRepository.save(newHistory);

        if (imageFiles != null && !imageFiles.isEmpty()) {
            // 1. MinIO에 파일들을 업로드하고 URL 목록을 받음
            List<String> imageUrls = imageFiles.stream()
                    .map(file -> {
                        try {
                            // fileStorageService를 통해 파일 업로드 후 URL 반환
                            return fileStorageService.uploadFile(file);
                        } catch (IOException e) {
                            // 실제 프로덕션에서는 에러 처리를 더 정교하게 해야 함
                            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
                        }
                    })
                    .toList();

            // 2. 각 URL을 HistoryImage 엔티티로 변환
            List<HistoryImage> historyImages = imageUrls.stream()
                    .map(url -> HistoryImage.builder()
                            .imageUrl(url)
                            .history(newHistory) // 방금 저장한 History 객체를 연결
                            .build())
                    .toList();

            // 3. HistoryImage 정보들을 DB에 한번에 저장
            historyImageRepository.saveAll(historyImages);
        }

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
    public void updateHistory(HistoryRequestDTO.HistoryUpdateRequest historyPatchRequest, List<MultipartFile> imageFiles, Long historyId){
        // (memberId 1이 사용자라고 가정)
        Long loginMemberId = 1L;
        Member member = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_MEMBER));
        History history = historyRepository.findByIdWithMember(historyId)
                .orElseThrow(()-> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));

        // 수정하려는 기록이 본인 기록이 아닐 때 예외 처리
        if (!history.isOwnedBy(member)) {
            throw new HistoryException(ErrorStatus.HISTORY_UPDATE_DENIED);
        }

        // 이미지 업로드 개수 제한 <= 10 and != 0
        validateImageCount(imageFiles);

        // 해시태그가 중복되는지 검사
        List<String> hashtagsList = historyPatchRequest.getHashtags();
        validateHasgtagsDuplicatoin(hashtagsList);

        // 옷이 중복되는지 검사 중복시 예외처리
        List<Long> clothesIdList = historyPatchRequest.getClothes();
        validateClothesDuplication(clothesIdList);

        ///// content 업데이트
        history.setContent(historyPatchRequest.getContent());

        ///// 기록-옷 매핑 테이블 업데이트 (기존꺼 모두 삭제 후 재등록)
        List<Cloth> newClothesList = validateAndGetClothesOwnership(clothesIdList, member);

        List<HistoryCloth> historyClothes = historyClothRepository.findAllByHistory(history);
        //중간 엔티티에서 Cloth 목록을 추출
        List<Cloth> clothes = historyClothes.stream()
                .map(HistoryCloth::getCloth)
                .collect(Collectors.toList());
        decreaseWearCounts(clothes); // 삭제 전 기존 옷들 wearNum -1

        historyClothRepository.deleteAllByHistory(history);
        List<HistoryCloth> newHistoryClothes = newClothesList.stream()
                .map(cloth -> HistoryCloth.builder()
                        .cloth(cloth)
                        .history(history)
                        .build())
                .toList();
        historyClothRepository.saveAll(newHistoryClothes);
        increaseWearCounts(newClothesList);

        ///// 해시태그 업데이트
        // DB에 존재하는 해시태그 조회
        List<Hashtag> existingHashtags = hashtagRepository.findAllByNameIn((hashtagsList));
        Set<String> existingTagNames = existingHashtags.stream()
                .map(Hashtag::getName)
                .collect(Collectors.toSet());

        // 없는 해시태그 추출
        List<Hashtag> newHashtags = hashtagsList.stream()
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
        // 기록의 기존 해시태그 모두 지우고 새로 업데이트
        hashtagHistoryRepository.deleteAllByHistory(history);
        hashtagHistoryRepository.saveAll(hashtagHistories);
    }

    @Override
    @Transactional
    public void updateComment(HistoryRequestDTO.CommentUpdateRequest commentUpdateRequest, Long commentId) {
        // (memberId 1이 사용자라고 가정)
        Long loginMemberId = 1L;
        Member member = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_MEMBER));
        Comment comment = commentRepository.findByIdWithMember(commentId)
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_COMMENT));

        // 수정하려는 댓글이 본인 댓글이 아닐 때 예외 처리
        if(!isOwnedBy(comment, member)) { // 이거 그냥 서비스에 private 메서드로 빼는게 좋을 것 같음
            throw new HistoryException((ErrorStatus.COMMENT_UPDATE_DENIED));
        }

        comment.setContent(commentUpdateRequest.getContent());
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

    // 이미지가 10개 초과하거나 없는 경우 예외처리 헬퍼 메서드
    private void validateImageCount(List<MultipartFile> imageFiles) {
        if (imageFiles.size() > 10) {
            throw new HistoryException(ErrorStatus.TOO_MANY_IMAGES);
        } else if (imageFiles.isEmpty()) {
            throw new HistoryException(ErrorStatus.NO_IMAGE_SENT);
        }
    }

    // 등록하려는 옷에 중복이 있는지 검사하는 헬퍼 메서드
    private void validateClothesDuplication(List<Long> clothesList) {
        Set<Long> set = new HashSet<>(clothesList);
        if (clothesList.size() != set.size()) throw new HistoryException(ErrorStatus.CLOTHES_NOT_UNIQUE);
    }

    // 등록하려는 해시태그가 중복되는지 검사하는 헬퍼 메서드
    private void validateHasgtagsDuplicatoin(List<String> hashtagsList) {
        if (hashtagsList == null || hashtagsList.isEmpty()) {
            return;
        }
        Set<String> set2 = new HashSet<>(hashtagsList);
        if (hashtagsList.size() != set2.size()) throw new HistoryException(ErrorStatus.HASHTAGS_NOT_UNIQUE);
    }

    // 옷의 소유권을 검증하고 검증된 옷을 반환하는 헬퍼메서드
    private List<Cloth> validateAndGetClothesOwnership(List<Long> clothIds, Member owner) {
        List<Cloth> ownedClothes = clothRepository.findAllByIdInAndMember(clothIds, owner);

        if (ownedClothes.size() != clothIds.size()) {
            throw new HistoryException(ErrorStatus.NO_OWNED_CLOTH);
        }
        return ownedClothes;
    }

    // 옷들의 착용 횟수 +1
    private void increaseWearCounts(List<Cloth> clothes) {
        for (Cloth cloth : clothes) {
            cloth.increaseWearNum(); // 엔티티 내부에 캡슐화된 메서드 호출
        }
    }

    // 옷들의 착용 횟수 -1
    private void decreaseWearCounts(List<Cloth> clothes) {
        for (Cloth cloth : clothes) {
            cloth.decreaseWearNum(); // 엔티티 내부에 캡슐화된 메서드 호출
        }
    }

    private boolean isOwnedBy(Comment comment, Member member) {
        // 현재 기록의 주인이 없거나, 비교 대상 멤버가 없으면 false
        if (comment.getMember() == null || member == null) {
            return false;
        }
        // Member 객체끼리 비교 (Member 클래스에 equals가 id 기준으로 구현되어 있어야 함)
        return comment.getMember().equals(member);
    }
}