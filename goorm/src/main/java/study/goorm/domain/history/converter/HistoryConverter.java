package study.goorm.domain.history.converter;

import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.member.domain.entity.Member;

import java.time.LocalDate;
import java.util.List;

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

    public static HistoryResponseDTO.HistoryCreateResult toHistoryCreateResult(History history){
        return HistoryResponseDTO.HistoryCreateResult.builder()
                .historyId(history.getId())
                .build();
    }
}
