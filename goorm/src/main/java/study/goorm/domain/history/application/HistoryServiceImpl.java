package study.goorm.domain.history.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.cloth.domain.repository.ClothRepository;
import study.goorm.domain.history.converter.HistoryConverter;
import study.goorm.domain.cloth.exception.ClothException;
import study.goorm.domain.history.domain.entity.*;
import study.goorm.domain.history.domain.repository.*;
import study.goorm.domain.history.dto.HistoryRequestDTO;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.history.exception.HistoryException;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.member.domain.repository.MemberRepository;
import study.goorm.global.error.code.status.ErrorStatus;
import study.goorm.global.exception.GeneralException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoryServiceImpl implements HistoryService{

    private final HistoryRepository historyRepository;
    private final HistoryImageRepository historyImageRepository;
    private final HashtagHistoryRepository hashtagHistoryRepository;
    private final HistoryClothRepository historyClothRepository;
    private final MemberRepository memberRepository;
    private final ClothRepository clothRepository;
    private final HashtagRepository hashtagRepository;
    private final S3Service s3Service;
    private final CommentRepository commentRepository;
    private final MemberLikeRepository memberLikeRepository;

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

    @Override
    @Transactional
    public Long createHistory(String clokeyId, HistoryRequestDTO.HistoryCreateDTO request, MultipartFile imageFile) {

        // 1. 회원 검증 및 조회
        Member member = memberRepository.findByClokeyId(clokeyId)
                .orElseThrow(() -> new ClothException(ErrorStatus.NO_SUCH_MEMBER));

        // 2. History 엔티티 생성 및 저장
        History newHistory = History.builder()
                .content(request.getContent())
                .historyDate(request.getDate())
                .member(member)
                .likes(0) // 초기 좋아요 수 0
                .build();
        historyRepository.save(newHistory);

        if (request.getContent() != null) {
            newHistory.setContent(request.getContent());
        }

        // 3. 이미지 업로드 및 HistoryImage 저장
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = s3Service.uploadFile(imageFile); // S3에 이미지 업로드
            HistoryImage historyImage = HistoryImage.builder()
                    .imageUrl(imageUrl)
                    .history(newHistory)
                    .build();
            historyImageRepository.save(historyImage);
        }

        // 4. 옷 착용 횟수 반영 및 HistoryCloth 저장
        if (request.getClothes() != null && !request.getClothes().isEmpty()) {
            List<Cloth> clothes = clothRepository.findAllByIdIn(request.getClothes()); // 요청된 모든 옷 ID에 대해 옷 엔티티 조회
            if (clothes.size() != request.getClothes().size()) {
                // 요청된 옷 ID 중 유효하지 않은 것이 있을 경우 예외 처리
                throw new ClothException(ErrorStatus.NO_SUCH_CLOTH); // 적절한 예외 메시지로 변경
            }
            for (Cloth cloth : clothes) {
                clothRepository.incrementWearNum(cloth.getId()); // wearNum 증가
                HistoryCloth historyCloth = HistoryCloth.builder()
                        .history(newHistory)
                        .cloth(cloth)
                        .build();
                historyClothRepository.save(historyCloth);
            }
        }

        // 5. 해시태그 처리 및 HashtagHistory 저장
        if (request.getHashtags() != null && !request.getHashtags().isEmpty()) {
            for (String tagName : request.getHashtags()) {
                Hashtag hashtag = hashtagRepository.findByName(tagName)
                        .orElseGet(() -> hashtagRepository.save(Hashtag.builder().name(tagName).build())); // 없으면 새로 생성
                HashtagHistory hashtagHistory = HashtagHistory.builder()
                        .history(newHistory)
                        .hashtag(hashtag)
                        .build();
                hashtagHistoryRepository.save(hashtagHistory);
            }
        }

        return newHistory.getId(); // 생성된 History의 ID 반환
    }

    @Override
    @Transactional
    public void updateHistory(
            Long historyId,
            String clokeyId,
            HistoryRequestDTO.HistoryUpdateDTO request,
            MultipartFile imageFile
    ) {

        // 1. History 조회 및 존재 여부 확인
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryException(ErrorStatus.HISTORY_NOT_FOUND));

        if (!history.getMember().getClokeyId().equals(clokeyId)) {
            throw new HistoryException(ErrorStatus._FORBIDDEN);
        }

        if (request.getContent() != null) {
            history.setContent(request.getContent());
        }


        // 3. 이미지 처리: 새로운 이미지가 제공된 경우에만 기존 이미지 삭제 및 업데이트
        if (imageFile != null && !imageFile.isEmpty()) {
            // 기존 이미지 삭제
            List<HistoryImage> oldImages = historyImageRepository.findByHistoryId(historyId);
            for (HistoryImage oldImage : oldImages) {
                try {
                    s3Service.deleteFile(oldImage.getImageUrl());
                } catch (GeneralException e) {
                    log.error("Failed to delete old S3 image {}: {}", oldImage.getImageUrl(), e.getMessage());
                    throw e; // 삭제 실패 시 예외를 다시 던져 트랜잭션 롤백
                }
                historyImageRepository.delete(oldImage);
            }

            // 새 이미지 업로드
            String imageUrl;
            try {
                imageUrl = s3Service.uploadFile(imageFile); // S3에 새 파일 업로드
            } catch (GeneralException e) {
                log.error("Failed to upload new S3 image: {}", e.getMessage());
                throw e; // 업로드 실패 시 예외를 다시 던져 트랜잭션 롤백
            }

            // imageUrl이 null이 아닐 때만 저장 (S3Service.uploadFile이 null을 반환할 수 있는 경우)
            if (imageUrl != null) {
                HistoryImage newImage = HistoryImage.builder()
                        .imageUrl(imageUrl)
                        .history(history)
                        .build();
                historyImageRepository.save(newImage);
            } else {
                log.warn("New image file {} was provided but upload failed. History updated without this image.", imageFile.getOriginalFilename());
                // 이미지 업로드 실패 시 경고만 남기고 계속 진행 (트랜잭션 롤백 안 함)
            }
        }

        // 4. 해시태그 업데이트
        if (request.getHashtags() != null && !request.getHashtags().isEmpty()) {

            hashtagHistoryRepository.deleteAllByHistory(history);

            List<HashtagHistory> newHashtagHistories = new ArrayList<>();
            for (String tagName : request.getHashtags()) {
                Hashtag hashtag = hashtagRepository.findByName(tagName)
                        .orElseGet(() -> {
                            Hashtag newHashtag = Hashtag.builder().name(tagName).build();
                            return hashtagRepository.save(newHashtag);
                        });

                HashtagHistory hashtagHistory = HashtagHistory.builder()
                        .hashtag(hashtag)
                        .history(history)
                        .build();
                newHashtagHistories.add(hashtagHistory);
            }
            hashtagHistoryRepository.saveAll(newHashtagHistories);
        }

        if (request.getClothes() != null && !request.getClothes().isEmpty()) {
            // 요청에 옷 목록이 존재하고 비어있지 않다면, 기존 HistoryCloth 레코드를 모두 삭제
            List<HistoryCloth> existingHistoryClothes = historyClothRepository.findByHistoryIdWithCloth(historyId);
            for (HistoryCloth existingHistoryCloth : existingHistoryClothes) {
                Cloth clothToDecrease = existingHistoryCloth.getCloth();
                if (clothToDecrease != null) {
                    clothToDecrease.decreaseWearCount();
                    clothRepository.save(clothToDecrease); // 감소된 착용 횟수 저장
                }
            }

            historyClothRepository.deleteAllByHistory(history);

            List<HistoryCloth> newHistoryClothes = new ArrayList<>();
            for (Long clothId : request.getClothes()) {
                Cloth cloth = clothRepository.findById(clothId)
                        .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_CLOTH));

                HistoryCloth historyCloth = HistoryCloth.builder()
                        .history(history)
                        .cloth(cloth)
                        .build();
                newHistoryClothes.add(historyCloth);

                cloth.increaseWearCount();
                clothRepository.save(cloth);
            }
            historyClothRepository.saveAll(newHistoryClothes);
        }

        // 6. 변경된 History 엔티티 최종 저장
        historyRepository.save(history);
    }

    @Override
    @Transactional
    public void deleteHistory(Long historyId, String clokeyId) {
        // 1. History 조회 및 존재 여부 확인
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryException(ErrorStatus.HISTORY_NOT_FOUND));

        // 사용자 인증
        if (!history.getMember().getClokeyId().equals(clokeyId)) {
            throw new HistoryException(ErrorStatus._FORBIDDEN);
        }

        // 2. 기록과 관련된 댓글 전부 삭제
        // Comment 엔티티에 history 필드가 있고, CommentRepository에 deleteAllByHistory(History history) 메서드가 필요
        commentRepository.deleteAllByHistory(history);
        log.info("History {} related comments deleted.", historyId);

        // 3. 기록과 관련된 좋아요 전부 삭제
        // Like 엔티티에 history 필드가 있고, LikeRepository에 deleteAllByHistory(History history) 메서드가 필요
        memberLikeRepository.deleteAllByHistory(history);
        log.info("History {} related likes deleted.", historyId);

        // 4. 기록-옷 테이블에서 기록과 관련된 row들을 전부 삭제하기 전에, 옷 착용 횟수 감소
        List<HistoryCloth> existingHistoryClothes = historyClothRepository.findByHistory(history);
        for (HistoryCloth existingHistoryCloth : existingHistoryClothes) {
            Cloth clothToDecrease = existingHistoryCloth.getCloth();
            if (clothToDecrease != null) {
                clothToDecrease.decreaseWearCount();
                clothRepository.save(clothToDecrease); // 감소된 착용 횟수 저장
                log.debug("Decreased wear count for Cloth ID: {}", clothToDecrease.getId());
            }
        }
        historyClothRepository.deleteAllByHistory(history);
        log.info("History {} related HistoryCloth entries deleted and wear counts decreased.", historyId);

        // 5. 기록과 관련된 Hashtag_history 전부 삭제
        hashtagHistoryRepository.deleteAllByHistory(history);
        log.info("History {} related HashtagHistory entries deleted.", historyId);

        // 6. 기록과 관련된 사진이 모두 삭제 (S3 및 DB)
        List<HistoryImage> historyImages = historyImageRepository.findByHistoryId(history.getId());
        for (HistoryImage image : historyImages) {
            try {
                s3Service.deleteFile(image.getImageUrl());
                log.debug("Deleted S3 image: {}", image.getImageUrl());
            } catch (GeneralException e) {
                log.error("Failed to delete S3 image {}: {}", image.getImageUrl(), e.getMessage());
                // S3 삭제 실패 시에도 DB 레코드 삭제는 진행 (멱등성 고려)
            }
            historyImageRepository.delete(image);
        }
        log.info("History {} related images deleted from S3 and DB.", historyId);

        // 7. 최종적으로 History 엔티티 삭제
        historyRepository.delete(history);
        log.info("History {} deleted successfully.", historyId);
    }
}