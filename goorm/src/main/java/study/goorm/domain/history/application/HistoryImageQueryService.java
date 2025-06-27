package study.goorm.domain.history.application;

import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.history.domain.entity.History;

import java.util.Map;

public interface HistoryImageQueryService {
    Map<Long, String> getFirstHistoryImageUrlMap(Iterable<History> histories);
}
