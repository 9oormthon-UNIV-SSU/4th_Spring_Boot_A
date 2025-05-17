package study.goorm.domain.cloth.application;

import study.goorm.domain.cloth.domain.entity.Cloth;

import java.util.Map;

public interface ClothImageQueryService {

    Map<Long, String> getFirstImageUrlMap(Iterable<Cloth> clothes);
}
