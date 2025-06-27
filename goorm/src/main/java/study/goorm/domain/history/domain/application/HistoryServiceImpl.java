package study.goorm.domain.history.domain.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import study.goorm.domain.cloth.domain.dto.ClothRequestDTO;
import study.goorm.domain.cloth.domain.entity.Cloth;
import study.goorm.domain.cloth.domain.exception.ClothException;
import study.goorm.domain.cloth.domain.repository.ClothRepository;
import study.goorm.domain.history.domain.converter.HistoryConverter;
import study.goorm.domain.history.domain.dto.HistoryRequestDTO;
import study.goorm.domain.history.domain.dto.HistoryResponseDTO;
import study.goorm.domain.history.domain.entity.*;
import study.goorm.domain.history.domain.exception.HistoryException;
import study.goorm.domain.history.domain.repository.*;
import study.goorm.domain.member.domain.application.MemberService;
import study.goorm.domain.member.domain.entity.Member;
import study.goorm.domain.member.domain.exception.MemberException;
import study.goorm.domain.member.domain.repository.MemberRepository;
import study.goorm.global.error.code.status.ErrorStatus;
import java.util.Optional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
    private final HistoryImageRepository historyImageRepository;
    private final HistoryConverter historyConverter;
    private final MemberService memberService;
    private final CommentRepository commentRepository;
    private final HashtagHistoryRepository hashtagHistoryRepository;
    private final HistoryClothRepository historyClothRepository;
    private final MemberLikeRepository memberLikeRepository;
    private final ClothRepository clothRepository;
    private final HashtagRepository hashtagRepository;
    private final MemberRepository memberRepository;

    // 상수 필드 생성자 매서드 순서 지켜서 넣기
    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.MonthlyHistoriesResult getMonthlyHistories(String clokeyId, YearMonth month){

        Member member = (clokeyId == null)
                ? memberService.getCurrentMember()
                : memberService.findByClokeyId(clokeyId)
                .orElseThrow(() -> new MemberException(ErrorStatus.NO_SUCH_MEMBER));

        List<History> histories = historyRepository.findByMemberIdAndMonth(member.getId(), month);

        // 이미지 로직 따로 빼는게 더 나을까요? 아니면 그냥 두는게 나을까요 -> 빼는 게 더 좋을 거같음(가독성 안좋음)
        List<HistoryResponseDTO.MonthlyHistoriesResult.HistoryDto> historiesDtos = histories.stream()
                .map(history -> {
                    String imageUrl = historyImageRepository
                            .findFirstByHistoryIdOrderByCreatedAtAsc(history.getId())
                            .map(HistoryImage::getImageUrl)
                            .orElseThrow(() -> new HistoryException(ErrorStatus.NO_CLOTH_IMAGE));
                    return historyConverter.toMonthlyHistoryDto(history, imageUrl);
                })
                .collect(Collectors.toList());

        return historyConverter.toMonthlyHistoriesResult(member, historiesDtos);
    }




    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.DailyHistoryResult getDailyHistory(Long historyId){

        Member currentMember = memberService.getCurrentMember();

        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));

        if(!history.getMember().getId().equals(currentMember.getId())){
            throw new HistoryException(ErrorStatus.NO_GRANT_HISTORY);
        }

        List<String> imageUrls = historyImageRepository.findAllByHistory(history)
                .stream()
                .map(HistoryImage::getImageUrl)
                .toList();

        List<String> hashtags = hashtagHistoryRepository.findAllByHistory(history)
                .stream()
                .map(hashtagHistory -> hashtagHistory.getHashtag().getName())
                .toList();
        // N+1 발생 가능성 있음 -> 패치조인 하거나 직접 꺼내오기

        boolean liked = memberLikeRepository.existsByHistoryAndMember(history, currentMember);


        List<HistoryResponseDTO.DailyHistoryResult.ClothDto> clothDtos = historyClothRepository.findAllByHistory(history)
                .stream()
                .map(mapping -> historyConverter.toClothDto(mapping.getCloth()))
                .toList();


        return historyConverter.toDailyHistoryResult(history, imageUrls, hashtags, liked, clothDtos);
    }

    // 기록 추가
    @Override
    public HistoryResponseDTO.HistoryCreateResult  createHistory(HistoryRequestDTO.HistoryCreateRequest historyCreateRequest, List<MultipartFile> imageFiles){

        Member member = memberService.getCurrentMember();
        LocalDate date = historyCreateRequest.getDate();

        // 있으면 에러 없으면 밑에서 만들도록 하는게 가독성이 좋을 듯
        History history = historyRepository.findByMemberAndHistoryDate(member,historyCreateRequest.getDate())
                .orElseGet(() -> History.builder()
                .historyDate(date)
                .content(historyCreateRequest.getContent())
                .likes(0)
                .member(member)
                .build());
        // Content 중복 분리하기
        history.setContent(historyCreateRequest.getContent());
        historyRepository.save(history);

        if(imageFiles == null || imageFiles.isEmpty()){
            throw new HistoryException(ErrorStatus.NO_HISTORY_IMAGE);
        }

        // S3 연결 안함 + 이미지 10개 이상인거 검증하기
        for (MultipartFile file : imageFiles) {
            String imageUrl = "image URL";
            HistoryImage image = HistoryImage.builder()
                    .imageUrl(imageUrl)
                    .history(history)
                    .build();
            historyImageRepository.save(image);
        }

        for(String tag : historyCreateRequest.getHashtags()){
            Hashtag hashtag = hashtagRepository.findByName(tag)
                    .orElseGet(() -> hashtagRepository.save(Hashtag.builder().name(tag).build()));


            HashtagHistory hashtagHistory = HashtagHistory.builder()
                    .history(history)
                    .hashtag(hashtag)
                    .build();
            hashtagHistoryRepository.save(hashtagHistory);
        }

        for(Long clothId : historyCreateRequest.getClothes()){ // for 문법 쓰는거보다 stream 쓰는게 가독성이 좋음 (한번에 찾아오도록)
            Cloth cloth = clothRepository.findById(clothId)
                    .orElseThrow(() -> new ClothException(ErrorStatus.NO_SUCH_CLOTH));

            cloth.setWearNum(cloth.getWearNum() + 1);
            clothRepository.save(cloth);

            HistoryCloth historyCloth = HistoryCloth.builder()
                    .cloth(cloth)
                    .history(history)
                    .build();
            historyClothRepository.save(historyCloth);
        }


        return historyConverter.toHistoryCreateResult(history);
    }

    // WearNum을 너무 자유롭게 변경하게 둠 (금준님 코드 참고하기)
    // 트렌젝션이 없어서 트렌적션이 없는 거에서 호출을 하면 위험할 수 있음 (에러 가능성)
    // 짧아서 분리할 필요도 없을 듯
    private void decrWear(Cloth cloth) {
        cloth.setWearNum(Math.max(cloth.getWearNum() - 1, 0));
        clothRepository.save(cloth);
    }

    private void incrWear(Cloth cloth) {
        cloth.setWearNum(cloth.getWearNum() + 1);
        clothRepository.save(cloth);
    }


    @Override
    @Transactional
    public void updateHistory(
            Long historyId,
            HistoryRequestDTO.HistoryUpdateRequest req,
            List<MultipartFile> imageFiles
    ) {
        Member me = memberService.getCurrentMember();
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));
        if (!history.getMember().getId().equals(me.getId())) {
            throw new HistoryException(ErrorStatus.NO_GRANT_HISTORY);
        }

        if (imageFiles == null || imageFiles.isEmpty()) {
            throw new HistoryException(ErrorStatus.NO_ENOUGH_IMAGES);
        }
        if (imageFiles.size() > 10) {
            throw new HistoryException(ErrorStatus.NO_ENOUGH_IMAGES);
        }

        if (req.getClothes().size() != new HashSet<>(req.getClothes()).size()
                || req.getHashtags().size() != new HashSet<>(req.getHashtags()).size()) {
            throw new HistoryException(ErrorStatus.NO_ONLY_CLOTH);
        }

        History updated = historyConverter.toUpdatedHistory(history, req);
        historyRepository.save(updated); // save 안해도 업데이트 됨 (트랜잭션안에있ㅇㅁ)

        historyImageRepository.deleteAllByHistory(updated);
        hashtagHistoryRepository.deleteAllByHistory(updated);
        List<HistoryCloth> olds = historyClothRepository.findAllByHistory(updated);
        for (HistoryCloth hc : olds) {
            decrWear(hc.getCloth());
        }
        historyClothRepository.deleteAll(olds);

        for (MultipartFile f : imageFiles) {
            String imageUrl = "uploaded-url";  // S3 후 변경 예정
            historyImageRepository.save(
                    historyConverter.toHistoryImage(updated, imageUrl)
            );
        }

        for (String tagName : req.getHashtags()) {
            Hashtag tag = hashtagRepository.findByName(tagName)
                    .orElseGet(() -> hashtagRepository.save(
                            Hashtag.builder().name(tagName).build()
                    ));
            hashtagHistoryRepository.save(
                    historyConverter.toHashtagHistory(updated, tag)
            );
        }

        for (Long clothId : req.getClothes()) {
            Cloth cloth = clothRepository.findById(clothId)
                    .filter(c -> c.getMember().equals(me))
                    .orElseThrow(() -> new HistoryException(ErrorStatus.NO_ONLY_CLOTH));

            incrWear(cloth);
            historyClothRepository.save(
                    historyConverter.toHistoryCloth(updated, cloth)
            );
        }
    }


    @Transactional
    @Override
    public void deleteHistory(Long historyId) {
        Member member = memberService.getCurrentMember();

        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));

        if (!history.getMember().getId().equals(member.getId())) {
            throw new HistoryException(ErrorStatus.NO_GRANT_HISTORY);
        }

        historyImageRepository.deleteAllByHistory(history);

        hashtagHistoryRepository.deleteAllByHistory(history);

        memberLikeRepository.deleteAllByHistory(history);

        commentRepository.deleteAllByHistory(history);

        List<HistoryCloth> mappings = historyClothRepository.findAllByHistory(history);
        for (HistoryCloth hc : mappings) { // 이것도 스트림으로 하기
            Cloth cloth = hc.getCloth();
            cloth.setWearNum(Math.max(0, cloth.getWearNum() - 1));
//            clothRepository.save(cloth); // 얘만 없애면 너무 비효율적일거같진않음
        }
        historyClothRepository.deleteAll(mappings);

        historyRepository.delete(history);
    }

    @Override
    @Transactional
        public HistoryResponseDTO.LikeResult like(Long memberId, HistoryRequestDTO.LikeRequest request) {
        // 기록 존재 확인
        History history = historyRepository.findById(request.getHistoryId())
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));

        Member member = memberService.getCurrentMember();

        // 현재 DB 상태 저장
        boolean currentlyLiked = memberLikeRepository.existsByHistoryAndMember(history, member);

        // 요청값과 실제 상태가 같으면 에러
        if (currentlyLiked == request.getLiked()) {
            throw new HistoryException(ErrorStatus.NO_STATE_LIKE);
        }

        boolean newLiked;

        if (currentlyLiked) {
            // 좋아요 취소
            MemberLike memberLike = memberLikeRepository
                    .findByHistoryAndMember(history, member)
                    .orElseThrow(() -> new HistoryException(ErrorStatus.NO_STATE_LIKE));
//            memberLikeRepository.delete(memberLike);
            history.setLikes(history.getLikes() - 1);
            newLiked = false;
        } else {
            // 좋아요 추가
            MemberLike newMemberLike = MemberLike.builder()
                    .history(history)
                    .member(member)
                    .build();
            history.setLikes(history.getLikes() + 1);
            newLiked = true;
        }

        // 좋아요 수 반영
        historyRepository.save(history);

        return historyConverter.toLikeResult(history, newLiked);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.LikedUsersResult getLikedUsers(Long historyId) {

        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));

        // 좋아요 누른 MemberLike 리스트 조회
        List<MemberLike> likes = memberLikeRepository.findAllWithMemberByHistory(history);

        // Member 객체만 꺼내 DTO 변환
        List<HistoryResponseDTO.LikedUserDto> users = likes.stream()
                .map(MemberLike::getMember)
                .map(historyConverter::toLikedUserDto)
                .collect(Collectors.toList());

        return HistoryResponseDTO.LikedUsersResult.builder()
                .likedUsers(users)
                .build();
    }

    @Override
    @Transactional
    public HistoryResponseDTO.CommentResult writeComment(
            Long memberId,
            Long historyId,
            HistoryRequestDTO.CommentRequestDTO request
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_MEMBER));

        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));


        Comment commentParent = null;

        // 대댓글 경우
        if (request.getCommentId() != null) {
            // 부모 댓글 존재 검증
            commentParent = commentRepository.findById(request.getCommentId())
                    .orElseThrow(() -> new HistoryException(ErrorStatus.NO_SUCH_HISTORY));

            // 대대댓글 방지
            if (commentParent.getComment() != null || !commentParent.getHistory().getId().equals(historyId)) {
                throw new HistoryException(ErrorStatus.NO_COMMENT_PLUS); // 잘못된 부모 댓글
            }
        }

        Comment comment = historyConverter.toEntity(history, member, commentParent, request);


        return historyConverter.toDto(comment);

    }

}
