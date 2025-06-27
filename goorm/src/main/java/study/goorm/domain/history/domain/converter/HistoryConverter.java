package study.goorm.domain.history.domain.converter;

import org.springframework.stereotype.Component;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.history.domain.dto.HistoryRequestDTO;
import study.goorm.domain.history.domain.dto.HistoryResponseDTO;
import study.goorm.domain.history.domain.entity.*;
import study.goorm.domain.member.domain.entity.Member;

import java.util.List;


@Component
public class HistoryConverter {
    public HistoryResponseDTO.MonthlyHistoriesResult.HistoryDto toMonthlyHistoryDto(History history, String imageUrl){
        return HistoryResponseDTO.MonthlyHistoriesResult.HistoryDto.builder()
                .historyId(history.getId())
                .date(history.getHistoryDate())
                .imageUrl(imageUrl)
                .build();
    }

    public HistoryResponseDTO.MonthlyHistoriesResult toMonthlyHistoriesResult(
            Member member, List<HistoryResponseDTO.MonthlyHistoriesResult.HistoryDto> historyDtos
    ){
        return HistoryResponseDTO.MonthlyHistoriesResult.builder()
                .id(member.getId())
                .nickName(member.getNickname())
                .histories(historyDtos)
                .build();
    }


    public HistoryResponseDTO.DailyHistoryResult toDailyHistoryResult(
            History history,
            List<String> imageUrls,
            List<String> hashtags,
            boolean liked,
            List<HistoryResponseDTO.DailyHistoryResult.ClothDto> clothDtos
            ){
        Member member = history.getMember();

        return HistoryResponseDTO.DailyHistoryResult.builder()
                .memberId(member.getId())
                .historyId(history.getId())
                .memberImageUrl(member.getProfileImageUrl())
                .nickName(member.getNickname())
                .clokeyId(member.getClokeyId())
                .contents(history.getContent())
                .imageUrls(imageUrls)
                .hashtags(hashtags)
                .likeCount(history.getLikes())
                .commentCount(0)
                .date(history.getHistoryDate().toString())
                .liked(liked)
                .cloths(clothDtos)
                .build();
    }

    public HistoryResponseDTO.DailyHistoryResult.ClothDto toClothDto(Cloth cloth){
        return HistoryResponseDTO.DailyHistoryResult.ClothDto.builder()
                .clothId(cloth.getId())
                .clothImageUrl(cloth.getClothUrl())
                .clothName(cloth.getName())
                .build();
    }

    // 기록 추가
    public static HistoryResponseDTO.HistoryCreateResult toHistoryCreateResult(History history){
        return HistoryResponseDTO.HistoryCreateResult.builder()
                .historyId(history.getId())
                .build();
    }

    // History 엔티티 업데이트
    // 컨버터에 로직이 들어가는건 지양하기
    public History toUpdatedHistory(History existing, HistoryRequestDTO.HistoryUpdateRequest req) {
        existing.setContent(req.getContent());
        existing.setVisibility(req.getVisibility());
        return existing;
    }

    // 이미지 매핑 엔티티 생성
    public HistoryImage toHistoryImage(History history, String imageUrl) {
        return HistoryImage.builder()
                .history(history)
                .imageUrl(imageUrl)
                .build();
    }

    // HashtagHistory 매핑 생성
    public HashtagHistory toHashtagHistory(History history, Hashtag hashtag) {
        return HashtagHistory.builder()
                .history(history)
                .hashtag(hashtag)
                .build();
    }

    // HistoryCloth 매핑 생성
    public HistoryCloth toHistoryCloth(History history, Cloth cloth) {
        return HistoryCloth.builder()
                .history(history)
                .cloth(cloth)
                .build();
    }

   