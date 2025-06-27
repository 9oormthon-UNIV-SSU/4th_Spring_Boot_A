package study.goorm.domain.history.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryCommentParamDTO {
    private Long commentId;
    private String content;
    private boolean isRoot;
    private Long parentId;
    private String clokeyId;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime createdAt;
}
