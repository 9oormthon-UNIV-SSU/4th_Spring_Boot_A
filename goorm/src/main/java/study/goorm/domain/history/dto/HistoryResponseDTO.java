package study.goorm.domain.history.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;

import java.util.List;

public class HistoryResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryMonthResult {
        private Long memberId;
        private String nickName;
        private List<HistoryImageDTO> histories;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryImageDTO {
        private Long historyId;
        private String date;
        private String imageUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryDayResult {
        private Long memberId;
        private Long historyId;
        private String memberImageUrl;
        private String nickName;
        private String clokeyId;
        private String contents; // JSON 필드명에 맞춰 'contents'로 변경
        private List<String> imageUrl; // 이미지 URL은 리스트로
        private List<String> hashtags;
        private int likeCount;
        private int commentCount;
        private String date;
        private List<ClothDTO> cloths;
        private boolean liked; // 'liked'는 boolean 타입

        // 내부에 ClothDTO 정의
        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ClothDTO {
            private Long clothId;
            private String clothImageUrl;
            private String clothName;
        }
    }
}
