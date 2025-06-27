package study.goorm.domain.history.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.goorm.domain.history.domain.entity.History;
import study.goorm.domain.history.domain.entity.HistoryImage;
import study.goorm.domain.history.domain.repository.HistoryImageRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class HistoryImageQueryServiceImpl implements HistoryImageQueryService {

    private final HistoryImageRepository historyImageRepository;

    @Override
    public Map<Long, String> getFirstHistoryImageUrlMap(Iterable<History> histories) {
        List<Long> historyIds = StreamSupport.stream(histories.spliterator(), false)
                .map(History::getId)
                .toList();

        List<HistoryImage> firstHistoryImages = historyImageRepository.findAllById(historyIds);
        return firstHistoryImages.stream()
                .collect(Collectors.toMap(
                        image -> image.getHistory().getId(),
                        HistoryImage::getImageUrl
                ));
    }
}
