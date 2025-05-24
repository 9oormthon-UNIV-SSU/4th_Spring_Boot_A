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
}
