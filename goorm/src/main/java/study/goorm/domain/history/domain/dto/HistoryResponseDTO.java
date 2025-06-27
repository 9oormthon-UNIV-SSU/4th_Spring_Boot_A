package study.goorm.domain.history.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class HistoryResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyHistoriesResult{
        private Long id;
        private String nickName;
        private List<HistoryDto> histories;

        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class HistoryDto {
            private Long historyId;
            private LocalDate date;
            private String imageUrl;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyHistoryResult{
        private Long memberId;
        private Long historyId;
        private String memberImageUrl;
        private String nickName;
        private String clokeyId;
        private String contents;
        private List<String> imageUrls;
        private List<String> hashtags;
        private int likeCount;
        private int commentCount; // 응답표에는 있어서 임시로 넣어뒀습니당
        private boolean liked;
        private String date; // 여기서 Localdate가 나은지 string이 나은지?
        private List<ClothDto> cloths;

        @Getter
        @Builder
        public static class ClothDto{
            private Long clothId;
            private String clothImageUrl;
            private String clothName;
        }



    }

    // 기록 추가
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryCreateResult {
        private Long historyId;
    }

