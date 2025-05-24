package study.goorm.domain.history.application;

import study.goorm.domain.history.domain.entity.History;

import java.util.Map;

public interface HistoryImageQueryService {
    Map<Long, String> getFirstImageUrlMap(Iterable<History> histories);
}
