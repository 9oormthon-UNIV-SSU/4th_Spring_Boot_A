package study.goorm.domain.history.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.goorm.domain.history.domain.entity.Hashtag;
import study.goorm.domain.history.domain.entity.HistoryImage;

import java.awt.*;
import java.util.List;

public class HistoryResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyHistoryResult {

        private Long memberId;
        private String nickName;
        private List<MonthlyHistoryItemResult> histories;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyHistoryItemResult {
        private Long historyId;
        private String date;
        private String img_url;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyHistoryResult {
        Long memberId;
        String memberImageUrl;
        String nickName;
        String clokeyId;
        String contents;
        List<HistoryImage> images;
        List<Hashtag> hashtags;
        int likeCount;
        boolean liked;
        String date;
        List<DailyHistoryItemResult> clothes;
        Long clothId;
        Long historyId;
    }
}
