package study.goorm.domain.history.converter;

import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.history.domain.entity.Hashtag;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.history.dto.HistoryResponseDTO;
import study.goorm.domain.member.domain.entity.Member;

import java.util.List;
import java.util.Map;

public class HistoryConverter {

    public static HistoryResponseDTO.MonthlyHistoryPreview toMonthlyHistoryPreview(Member member, List<HistoryResponseDTO.MonthlyHistoryItemResult> result) {

        return HistoryResponseDTO.MonthlyHistoryPreview.builder()
                .memberId(member.getId())
                .nickName(member.getNickname())
                .histories(result)
                .build();
    }

    public static HistoryResponseDTO.DailyHistoryPreview toDailyHistoryPreview(
            Member member,
            History history,
            List<String> images,
            List<String> hashtags,
            long commentCount,
            List<HistoryResponseDTO.DailyHistoryClothesPreview> cloths
    ) {

        return HistoryResponseDTO.DailyHistoryPreview.builder()
                .memberId(member.getId())
                .historyId(history.getId())
                .memberImageUrl(member.getProfileImageUrl())
                .nickName(member.getNickname())
                .clokeyId(member.getClokeyId())
                .contents(history.getContent())
                .imageUrl(images)
                .hashtags(hashtags)
                .likeCount(history.getLikes())
                .commentCount(commentCount)
                .date(history.getHistoryDate())
                .cloths(cloths)
                .build();
    }

    public static HistoryResponseDTO.HistoryCreateResult toHistoryCreateResult(History history) {
        return HistoryResponseDTO.HistoryCreateResult.builder()
                .historyId(history.getId())
                .build();
    }

}
