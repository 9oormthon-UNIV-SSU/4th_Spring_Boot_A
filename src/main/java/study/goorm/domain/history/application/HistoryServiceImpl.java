package study.goorm.domain.history.application;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.cloth.domain.repository.ClothRepository;
import study.goorm.domain.history.converter.HistoryConverter;
import study.goorm.domain.history.domain.entity.*;
import study.goorm.domain.history.domain.repository.*;
import study.goorm.domain.history.dto.HistoryRequestDTO;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.history.exception.HistoryException;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.member.domain.repository.MemberRepository;
import study.goorm.global.error.code.status.ErrorStatus;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final MemberRepository memberRepository;
    private final HistoryRepository historyRepository;
    private final HistoryImageRepository historyImageRepository;
    private final HashtagHistoryRepository hashtagHistoryRepository;
    private final CommentRepository commentRepository;
    private final ClothRepository clothRepository;
    private final HistoryClothRepository historyClothRepository;
    private final HashtagRepository hashtagRepository;
    private final MinioClient minioClient;

    // 월별 기록 조회
    @Transactional(readOnly = true)
    @Override
    public HistoryResponseDTO.MonthlyHistoryPreview getMonthlyPreview(String clokeyId, String date) {

        // 날짜 형식 검증
        try {
            YearMonth.parse(date); // YYYY-MM 형식 검증만
        } catch (DateTimeParseException e) {
            throw new HistoryException(ErrorStatus.INVALID_DATE_FORMAT);
        }

        // clokeyId가 null이면 자신의 기록 열람
        Member member;
        if (clokeyId == null) {
            member = memberRepository.findById(1L)
                    .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_MEMBER));
        } else {
            member = memberRepository.findByClokeyId(clokeyId)
                    .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_MEMBER));
        }

        // 기록 조회
        List<History> histories = historyRepository.findHistoriesByMemberIdAndYearMonth(member.getId(), date);
        if (histories.isEmpty()) {
            throw new HistoryException(ErrorStatus.NO_SUCH_HISTORY);
        }


        List<Long> historyIds = histories.stream()
                .map(History::getId)
                .collect(Collectors.toList());

        // 사진 조회
        List<HistoryImage> historyImages = historyImageRepository.findAllByHistoryIdIn(historyIds);

        // historyId → 첫 번째 이미지 URL
        Map<Long, String> firstImagesOfHistory = historyImages.stream()
                .collect(Collectors.groupingBy(
                        img -> img.getHistory().getId(),
                        Collectors.mapping(HistoryImage::getImageUrl, Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.isEmpty() ? "비공개입니다" : list.get(0)
                        ))
                ));

        List<HistoryResponseDTO.MonthlyHistoryItemResult> resultList = histories.stream()
                .map(history -> HistoryResponseDTO.MonthlyHistoryItemResult.builder()
                        .historyId(history.getId())
                        .date(history.getHistoryDate().toString())
                        .imageUrl(firstImagesOfHistory.getOrDefault(history.getId(), "비공개입니다"))
                        .build())
                .collect(Collectors.toList());

        return HistoryConverter.toMonthlyHistoryPreview(member, resultList);
    }

    // 일별 기록 조회
    @Transactional(readOnly = true)
    @Override
    public HistoryResponseDTO.DailyHistoryPreview getDailyPreview(Long historyId) {

        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));

        Member member = history.getMember();

        // 이미지 조회
        List<HistoryImage> historyImages = historyImageRepository.findByHistoryId(historyId);
        List<String> images = historyImages.stream()
                .map(HistoryImage::getImageUrl)
                .toList();

        // 해시태그 조회
        List<HashtagHistory> hashtagHistories = hashtagHistoryRepository.findByHistoryId(historyId);

        // 기록의 해시태그들 조회
        List<Hashtag> hashtags = hashtagHistories.stream()
                .map(HashtagHistory::getHashtag)
                .toList(); // .collect(Collectors.toList()이랑 동일

        List<String> hashtagNameList = hashtags.stream()
                .map(Hashtag::getName)
                .toList();

        // 댓글 갯수 세기
        long commentCount = commentRepository.countByHistoryId(historyId);

        List<Cloth> cloths =  clothRepository.findByMemberId(member.getId());

        List<HistoryResponseDTO.DailyHistoryClothesPreview> clothPreviews = cloths.stream()
                .map(cloth -> HistoryResponseDTO.DailyHistoryClothesPreview.builder()
                        .clothId(cloth.getId())
                        .clothImageUrl(cloth.getClothUrl())  // 이미지 필드 맞게 수정
                        .clothName(cloth.getName())
                        .build())
                .toList();


        return HistoryConverter.toDailyHistoryPreview(member, history, images, hashtagNameList, commentCount, clothPreviews);
    }

    // 날짜별 옷 기록 추가
    @Transactional
    @Override
    public HistoryResponseDTO.HistoryCreateResult createHistory(HistoryRequestDTO.HistoryCreateRequest historyCreateRequest, List<MultipartFile> image) {

        // 이미지 업로드 개수 제한
        if (image.size() >= 10) {
            throw new HistoryException(ErrorStatus.TOO_MANY_IMAGES);
        }

        // member 1번이 로그인 한 유저라고 가정
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_MEMBER));

        // history 테이블에 내용, 날짜 저장
        History history = History.builder()
                .member(member)
                .content(historyCreateRequest.getContent())
                .historyDate(historyCreateRequest.getDate())
                .build();

        historyRepository.save(history);

        // clothId 검증
        List<Long> requestedIds = historyCreateRequest.getClothes();

        for (Long id : requestedIds) {
            boolean exists = clothRepository.existsById(id);
            if (!exists) {
                throw new HistoryException(ErrorStatus.NO_SUCH_CLOTH);
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

        List<String> requestedTags = historyCreateRequest.getHashtags();

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


        // MinIO 업로드
        for (MultipartFile images : image) {

            // 파일명 생성
            String fileName = UUID.randomUUID() + "_" + images.getOriginalFilename();
            String bucket = "history-image";

            try {
                // 버킷 없으면 생성
                if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                }

                // 업로드 실행
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucket)
                                .object(fileName)
                                .stream(images.getInputStream(), images.getSize(), -1)
                                .contentType(images.getContentType())
                                .build()
                );

                // URL 생성
                String imageUrl = "http://localhost:9000/" + bucket + "/" + fileName;

                // HistoryImage 저장
                HistoryImage historyImage = HistoryImage.builder()
                        .history(history)
                        .imageUrl(imageUrl)
                        .build();
                historyImageRepository.save(historyImage);

            } catch (Exception e) {
                throw new HistoryException(ErrorStatus.MINIO_UPLOAD_FAILED);
            }
        }

        return HistoryConverter.toHistoryCreateResult(history);
    }

    // 날짜별 옷 기록 수정
    @Transactional
    @Override
    public HistoryResponseDTO.HistoryUpdateResult updateHistory(HistoryRequestDTO.HistoryUpdateRequest request, List<MultipartFile> images, Long historyId) {

        if (images.size() >= 10) {
            throw new HistoryException(ErrorStatus.TOO_MANY_IMAGES);
        }

        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_MEMBER));

        // 기존 히스토리 조회
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));

        // content & visibility 수정
        history.update(request.getContent());

        // 기존 clothes & hashtag 관계 삭제
        historyClothRepository.deleteByHistory(history); // custom deleteByHistory
        hashtagHistoryRepository.deleteByHistory(history); // custom deleteByHistory

        // 새로운 clothes 저장
        List<Long> clothIds = request.getClothes();
        List<Cloth> clothes = clothRepository.findAllById(clothIds);

        List<HistoryCloth> historyClothes = clothes.stream()
                .map(cloth -> HistoryCloth.builder()
                        .history(history)
                        .cloth(cloth)
                        .build())
                .toList();
        historyClothRepository.saveAll(historyClothes);

        // 새로운 해시태그 저장 (새로운 해시태그 생성 포함)
        List<String> tagNames = request.getHashtags();

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

        // MinIO에서도 삭제
        for (HistoryImage hi : existingImages) {
            String url = hi.getImageUrl();
            String bucket = "history-image";

            // objectName 추출
            String objectName = null;
            try {
                objectName = url.substring(url.lastIndexOf("/") + 1);
            } catch (Exception ex) {
                System.err.println("❌ URL에서 objectName 추출 실패: " + url);
                continue;
            }

            // MinIO에서 삭제 시도
            try {
                System.out.println("🗑️ MinIO 삭제 시도: " + objectName);

                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucket)
                                .object(objectName)
                                .build()
                );

                System.out.println("✅ 삭제 성공: " + objectName);
            } catch (Exception e) {
                System.err.println("❌ 삭제 실패: " + objectName + ", 이유: " + e.getMessage());
                throw new HistoryException(ErrorStatus.MINIO_DELETE_FAILED); // 정의 필요
            }
        }


        // DB에서 HistoryImage 삭제
        historyImageRepository.deleteAllByHistory(history);

        // 새 이미지 업로드 및 저장
        for (MultipartFile file : images) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String bucket = "history-image";

            try {
                if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                }

                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucket)
                                .object(fileName)
                                .stream(file.getInputStream(), file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );

                String imageUrl = "http://localhost:9000/" + bucket + "/" + fileName;

                HistoryImage newImage = HistoryImage.builder()
                        .history(history)
                        .imageUrl(imageUrl)
                        .build();

                historyImageRepository.save(newImage);

            } catch (Exception e) {
                throw new HistoryException(ErrorStatus.MINIO_UPLOAD_FAILED);
            }
        }


        return HistoryConverter.toHistoryUpdateResult(history);
    }

    @Transactional
    @Override
    public void deleteHistory(Long historyId) {

        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));

        // Minio 이미지 삭제 위해 historyImage 조회
        List<HistoryImage> images = historyImageRepository.findAllByHistory(history);
        String bucket = "history-image";

        for (HistoryImage hi : images) {
            String url = hi.getImageUrl();
            String objectName = url.substring(url.lastIndexOf("/") + 1);

            try {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucket)
                                .object(objectName)
                                .build()
                );
            } catch (Exception e) {
                throw new HistoryException(ErrorStatus.MINIO_DELETE_FAILED);
            }
        }

        // 매핑 테이블 삭제
        historyClothRepository.deleteAllByHistory(history);       // 옷 매핑 삭제
        hashtagHistoryRepository.deleteAllByHistory(history);     // 해시태그 매핑 삭제
        historyImageRepository.deleteAllByHistory(history);       // 이미지 DB에서 삭제

        // 해당 기록 삭제
        historyRepository.delete(history);
    }
}
