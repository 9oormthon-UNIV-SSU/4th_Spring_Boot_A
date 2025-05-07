package study.goorm.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ThicknessLevel {
    LEVEL_0(0, "나시, 반팔, 반바지 등"),
    LEVEL_1(1, "긴팔, 셔츠, 슬랙스 등 얇은 소재"),
    LEVEL_2(2, "기모 無 맨투맨, 후드티 등"),
    LEVEL_3(3, "기모 有 맨투맨, 후드티, 가디건, 니트 등"),
    LEVEL_4(4, "코트, 무스탕 등"),
    LEVEL_5(5, "패딩 등 두꺼운 아우터");

    private final int value;
    private final String description;
}
