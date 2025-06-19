package study.goorm.domain.history.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.cloth.domain.entity.ClothImage;
import study.goorm.domain.cloth.domain.repository.ClothImageRepository;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.history.domain.repository.HistoryImageRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class HistoryImageQueryServiceImpl implements HistoryImageQueryService{

    private final HistoryImageRepository historyImageRepository;

    @Override
    public Map<Long, String> getFirstImageUrlMap(Iterable<History> histories) {

        List<Long> historyIds = StreamSupport.stream(histories.spliterator(), false)
                .map(History::getId)
                .toList();

        // history_id 기준으로 첫 이미지만 가져오는 쿼리 (IN 절 + group by 또는 distinct 필요)
        List<HistoryImage> firstImages = historyImageRepository.findFirstImagesByHistoryIds(historyIds);

        return firstImages.stream()
                .collect(Collectors.toMap(
                        image -> image.getHistory().getId(),
                        HistoryImage::getImageUrl
                ));
    }
}
