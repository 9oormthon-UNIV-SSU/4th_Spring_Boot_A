package study.goorm.domain.history.application;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
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

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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


        return HistoryConverter.toDailyHistoryPreview(member, history,images, hashtagNameList, commentCount,clothPreviews);
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

        // hashtagName 검증
        List<String> requestedTags = historyCreateRequest.getHashtags();

        for (String tagName : requestedTags) {
            boolean exists = hashtagRepository.existsByName(tagName);
            if (!exists) {
                throw new HistoryException(ErrorStatus.NO_SUCH_HASHTAG);
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

        // Hashtag 기록
        List<Hashtag> hashtags = hashtagRepository.findAllByNameIn(requestedTags);

        List<HashtagHistory> hashtagHistories = hashtags.stream()
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
}
