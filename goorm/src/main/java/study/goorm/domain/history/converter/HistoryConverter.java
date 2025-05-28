package study.goorm.domain.history.converter;

import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.cloth.domain.entity.Cloth;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryConverter {

    public static HistoryResponseDTO.HistoryGetMonthly toHistoryGetMonthly(Member member, LocalDate month, List<History> histories, List<HistoryImage> historyImages){
        return HistoryResponseDTO.HistoryGetMonthly.builder()
                .memberId(member.getId())
                .nickName(member.getNickname())
                .histories(toHistoryGetList(histories, historyImages))
                .build();
    }

    private static List<HistoryResponseDTO.HistoryGet> toHistoryGetList(List<History> histories, List<HistoryImage> historyImages) {
        return histories.stream()
                .map(history -> {
                    HistoryImage image = historyImages.stream()
                            .filter(img -> img.getHistory().getId().equals(history.getId()))
                            .findFirst()
                            .orElse(null);
                    return toHistoryGet(history, image);
                })
                .collect(java.util.stream.Collectors.toList());
    }

    private static HistoryResponseDTO.HistoryGet toHistoryGet(History history, HistoryImage historyImage){
        return HistoryResponseDTO.HistoryGet.builder()
                .historyId(history.getId())
                .date(history.getHistoryDate())
                .imageUrl(historyImage != null ? historyImage.getImageUrl() : "비공개입니다")
                .build();
    }

    public static HistoryResponseDTO.HistoryGetDaily toHistoryGetDaily(History history, List<HistoryImage> historyImage, Member member, List<String> hashtags, List<Cloth> cloths){
        return HistoryResponseDTO.HistoryGetDaily.builder()
                .memberId(member.getId())
                .historyId(history.getId())
                .memberImageUrl(member.getProfileImageUrl())
                .nickName(member.getNickname())
                .clokeyId(member.getClokeyId())
                .content(history.getContent())
                .images(historyImage)
                .hashtags(hashtags) // 해시태그는 외부에서 받아서 설정
                .likeCount(history.getLikes()) // 기본값 또는 실제 값 추가 필요
                .commentCount(history.getComments()) // 기본값 또는 실제 값 추가 필요
                .date(history.getHistoryDate())
                .clothes(toHistoryDailyList(cloths)) // 실제 의류 정보 변환
                .liked(false) // 실제 좋아요 여부는 사용자 컨텍스트 필요
                .build();
    }

    private static List<HistoryResponseDTO.HistoryGetCloth> toHistoryDailyList(List<Cloth> cloths) {
        return cloths.stream()
                .map(cloth -> HistoryResponseDTO.HistoryGetCloth.builder()
                        .clothId(cloth.getId())
                        .clothImageUrl(cloth.getClothUrl())
                        .clothName(cloth.getName())
                        .build())
                .collect(Collectors.toList());
    }

    public static HistoryResponseDTO.HistoryCreateResult toHistoryCreateResult(History history){
        return HistoryResponseDTO.HistoryCreateResult.builder()
                .historyId(history.getId())
                .build();
    }
}
