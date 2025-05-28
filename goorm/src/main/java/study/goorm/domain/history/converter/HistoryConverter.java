package study.goorm.domain.history.converter;

import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.cloth.domain.entity.ClothImage;
import study.goorm.domain.cloth.dto.ClothResponseDTO;
import study.goorm.domain.history.domain.entity.Hashtag;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.member.domain.entity.Member;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HistoryConverter {
    public static HistoryResponseDTO.MonthlyHistoryResult toMonthlyHistoryResult(Member member, Map<Long, String> firstImagesOfHistory, List<History> histories){
        return HistoryResponseDTO.MonthlyHistoryResult.builder()
                .memberId(member.getId())
                .nickName(member.getNickname())
                .histories(toMonthlyHistoryItemResult(firstImagesOfHistory, histories))
                .build();
    }

    public static List<HistoryResponseDTO.MonthlyHistoryItemResult> toMonthlyHistoryItemResult(Map<Long, String> firstImagesOfHistory, List<History> histories) {
        return histories.stream()
                .map(history ->  HistoryResponseDTO.MonthlyHistoryItemResult.builder()
                        .historyId(history.getId())
                        .date(history.getHistoryDate().toString())
                        .img_url(firstImagesOfHistory.get(history.getId()))
                        .build()
                )
                .collect(Collectors.toList());
    }

    public static HistoryResponseDTO.DailyHistoryResult toDailyHistoryResult(
            History history,
            Member member,
            boolean liked,
            List<String> historyImageUrls,
            List<String> hashtagNames,
            List<Cloth> clothes,
            Map<Long, String> firstImagesOfCloth
    ){
        return HistoryResponseDTO.DailyHistoryResult.builder()
                .memberId(history.getMember().getId())
                .memberImageUrl(member.getProfileImageUrl())
                .nickName(member.getNickname())
                .clokeyId(member.getClokeyId())
                .contents(history.getContent())
                .images(historyImageUrls)
                .hashtags(hashtagNames)
                .likeCount(history.getLikes())
                .liked(liked)
                .date(history.getHistoryDate())
                .clothes(toDailyHistoryItemResult(clothes, firstImagesOfCloth))
                .build();
    }

    public static List<HistoryResponseDTO.DailyHistoryItemResult> toDailyHistoryItemResult(List<Cloth> clothes, Map<Long, String> firstImagesOfCloth){
        return clothes.stream()
                .map(cloth -> HistoryResponseDTO.DailyHistoryItemResult.builder()
                .clothId(cloth.getId())
                .clothName(cloth.getName())
                .clothImageUrl(firstImagesOfCloth.get(cloth.getId()))
                .build()
                )
                .collect(Collectors.toList());
    }

    public static HistoryResponseDTO.HistoryCreateResult toHistoryCreateResult(History history){
        return HistoryResponseDTO.HistoryCreateResult.builder()
                .id(history.getId())
                .build();
    }
}
