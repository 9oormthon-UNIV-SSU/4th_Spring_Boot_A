package study.goorm.domain.history.converter;

import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.member.domain.entity.Member;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HistoryConverter {
    public static HistoryResponseDTO.HistoryMonthResult toHistoryMonthResult(Member member, List<HistoryImage> fetchedHistoryImages){

        // 1. History별로 가장 먼저 발견된 (ID가 낮은) HistoryImage를 선택합니다.
        Map<History, HistoryImage> firstImagePerHistory = fetchedHistoryImages.stream()
                .collect(Collectors.toMap(
                        HistoryImage::getHistory,
                        hi -> hi,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        // 2. Map의 각 엔트리를 HistoryResponseDTO.HistoryDTO로 변환합니다.
        List<HistoryResponseDTO.HistoryImageDTO> historyDTOs = firstImagePerHistory.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getHistoryDate())) // 최종적으로 HistoryDate 기준으로 정렬
                .map(entry -> {
                    History history = entry.getKey();
                    HistoryImage firstImage = entry.getValue();

                    String imageUrl = firstImage.getImageUrl();

                    return HistoryResponseDTO.HistoryImageDTO.builder()
                            .historyId(history.getId())
                            .date(history.getHistoryDate().toString())
                            .imageUrl(imageUrl)
                            .build();
                })
                .collect(Collectors.toList());

        // 3. 최종 HistoryMonthResult DTO를 빌드하여 반환합니다.
        return HistoryResponseDTO.HistoryMonthResult.builder()
                .memberId(member.getId())
                .nickName(member.getNickname())
                .histories(historyDTOs)
                .build();
    }

    public static HistoryResponseDTO.HistoryDayResult toHistoryDayResult( // 메소드명 변경
                                                                          History history,
                                                                          Member authorMember,
                                                                          List<String> imageUrls,
                                                                          List<String> hashtags,
                                                                          List<HistoryResponseDTO.HistoryDayResult.ClothDTO> clothDTOs, // DTO 클래스명 변경
                                                                          int commentCount,
                                                                          int likeCount,
                                                                          boolean liked
    ) {
        return HistoryResponseDTO.HistoryDayResult.builder() // DTO 클래스명 변경
                .memberId(authorMember.getId())
                .historyId(history.getId())
                .memberImageUrl(authorMember.getProfileImageUrl())
                .nickName(authorMember.getNickname())
                .clokeyId(authorMember.getClokeyId())
                .contents(history.getContent())
                .imageUrl(imageUrls)
                .hashtags(hashtags)
                .likeCount(likeCount)
                .commentCount(commentCount)
                .date(history.getHistoryDate().toString())
                .cloths(clothDTOs)
                .liked(liked)
                .build();
    }
}