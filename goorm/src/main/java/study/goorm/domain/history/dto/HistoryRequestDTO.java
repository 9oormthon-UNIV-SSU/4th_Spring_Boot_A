package study.goorm.domain.history.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public class HistoryRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistoryCreateDTO {
        private String content; // 게시글 내용
        private List<Long> clothes; // 옷 ID 리스트
        private List<String> hashtags; // 해시태그 이름 리스트

        @DateTimeFormat(pattern = "yyyy-MM-dd") // "2025-01-25" 형식 파싱을 위한 어노테이션
        private LocalDate date; // 기록 날짜 (YYYY-MM-DD 형태)
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryUpdateDTO { // HistoryCreateDTO와 유사하지만, 업데이트 목적임을 명시
        private String content;
        private List<Long> clothes; // 옷 ID 리스트
        private List<String> hashtags; // 해시태그 리스트
    }
}
