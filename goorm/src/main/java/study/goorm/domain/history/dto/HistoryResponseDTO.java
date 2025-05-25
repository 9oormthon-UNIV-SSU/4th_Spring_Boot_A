package study.goorm.domain.history.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.cloth.dto.ClothResponseDTO;
import study.goorm.domain.history.domain.entity.Hashtag;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<Image> images;
        List<Hashtag> hashtags;
        int likeCount;
        boolean liked;
        String date;
        List<Cloth> clothes;
        Long clothId;
        Long historyId;
    }
}
