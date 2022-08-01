package com.photory.service.feed;

import com.photory.common.exception.model.ForbiddenException;
import com.photory.common.exception.model.NotFoundException;
import com.photory.controller.feed.dto.request.DeleteFeedRequestDto;
import com.photory.controller.feed.dto.request.ModifyFeedRequestDto;
import com.photory.controller.feed.dto.response.GetFeedResponse;
import com.photory.controller.feed.dto.response.GetFeedsResponse;
import com.photory.controller.feed.dto.response.ModifyFeedResponse;
import com.photory.domain.collection.FeedImageCollection;
import com.photory.domain.common.collection.ScrollPaginationCollection;
import com.photory.domain.feed.Feed;
import com.photory.domain.feed.repository.FeedRepository;
import com.photory.domain.feedimage.FeedImage;
import com.photory.domain.feedimage.repository.FeedImageRepository;
import com.photory.domain.participate.Participate;
import com.photory.domain.participate.repository.ParticipateRepository;
import com.photory.domain.room.Room;
import com.photory.domain.room.repository.RoomRepository;
import com.photory.domain.user.User;
import com.photory.domain.user.repository.UserRepository;
import com.photory.service.image.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.photory.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ParticipateRepository participateRepository;
    private final FeedRepository feedRepository;
    private final FeedImageRepository feedImageRepository;
    private final S3Service s3Service;

    public void createFeed(String userEmail, List<MultipartFile> images, Long roomId, String title, String content) {
        User user = FeedServiceUtils.findUserByEmail(userRepository, userEmail);
        Room room = FeedServiceUtils.findRoomByRoomId(roomRepository, roomId);

        // 참여하고 있는 방이 아니면 피드 생성할 수 없음
        Optional<Participate> participate = participateRepository.findByRoomAndUser(room, user);
        if (participate.isEmpty()) {
            throw new ForbiddenException(String.format("방 (%s) 에 유저 (%s) 가 참여중이 아닙니다.", room.getId(), user.getId()), FORBIDDEN_ROOM_PARTICIPANT_EXCEPTION);
        }

        Feed feed = Feed.of(room, user, title, content);

        Feed savedFeed = feedRepository.save(feed);

        List<String> fileUrlList = s3Service.uploadFile(images);
        fileUrlList.forEach(file -> {
            FeedImage feedImage = FeedImage.of(savedFeed, file);

            feedImageRepository.save(feedImage);
        });
    }

    public GetFeedResponse getFeed(String userEmail, Long feedId) {
        User user = FeedServiceUtils.findUserByEmail(userRepository, userEmail);

        Optional<Feed> feed = feedRepository.findById(feedId);
        if (feed.isEmpty()) {
            throw new NotFoundException(String.format("존재하지 않는 피드 (%s) 입니다", feedId), NOT_FOUND_FEED_EXCEPTION);
        }

        Room room = feed.get().getRoom();

        //방에 참가한 사람만 피드 조회할 수 있음
        Optional<Participate> participating = participateRepository.findByRoomAndUser(room, user);
        if (participating.isEmpty()) {
            throw new ForbiddenException(String.format("방 (%s) 에 유저 (%s) 가 참여중이 아닙니다.", room.getId(), user.getId()), FORBIDDEN_ROOM_PARTICIPANT_EXCEPTION);
        }

        ArrayList<String> imageUrls = new ArrayList<>();
        ArrayList<FeedImage> images = feedImageRepository.findAllByFeed(feed.get());
        images.forEach(image -> {
            imageUrls.add(imageUrls.size(), image.getImageUrl());
        });

        GetFeedResponse response = GetFeedResponse.of(feed.get(), imageUrls);

        return response;
    }

    public GetFeedsResponse getFeeds(String userEmail, Long roomId, int size, Long lastFeedId) {
        User user = FeedServiceUtils.findUserByEmail(userRepository, userEmail);
        Room room = FeedServiceUtils.findRoomByRoomId(roomRepository, roomId);

        PageRequest pageRequest = PageRequest.of(0, size + 1);
        Page<Feed> page = feedRepository.findAllByRoomAndIdLessThanOrderByIdDesc(room, lastFeedId, pageRequest);
        List<Feed> feeds = page.getContent();

        ScrollPaginationCollection<Feed> feedsCursor = ScrollPaginationCollection.of(feeds, size);
        GetFeedsResponse response = GetFeedsResponse.of(feedsCursor, FeedImageCollection.of(feeds, feedImageRepository), feedRepository.countAllByRoom(room));

        return response;
    }

    public ModifyFeedResponse modifyFeed(String userEmail, ModifyFeedRequestDto request) {
        User user = FeedServiceUtils.findUserByEmail(userRepository, userEmail);
        Long feedId = request.getFeedId();
        String title = request.getTitle();
        String content = request.getContent();

        Optional<Feed> feed = feedRepository.findById(feedId);
        if (feed.isEmpty()) {
            throw new NotFoundException(String.format("존재하지 않는 피드 (%s) 입니다", feedId), NOT_FOUND_FEED_EXCEPTION);
        }

        // 피드 작성자가 아니면 수정할 수 없음
        if (feed.get().getUser().getId() != user.getId()) {
            throw new ForbiddenException(String.format("유저 (%s) 는 피드 (%s) 의 작성자가 아닙니다.", user.getId(), feedId), FORBIDDEN_FEED_OWNER_EXCEPTION);
        }

        feed.get().setTitle(title);
        feed.get().setContent(content);

        Feed modified = feedRepository.save(feed.get());

        ArrayList<String> imageUrls = new ArrayList<>();
        ArrayList<FeedImage> feedImages = feedImageRepository.findAllByFeed(feed.get());
        feedImages.forEach(image -> {
            imageUrls.add(imageUrls.size(), image.getImageUrl());
        });

        ModifyFeedResponse response = ModifyFeedResponse.of(modified, imageUrls);

        return response;
    }

    public void deleteFeed(String userEmail, DeleteFeedRequestDto request) {
        User user = FeedServiceUtils.findUserByEmail(userRepository, userEmail);
        Long feedId = request.getFeedId();

        Optional<Feed> feed = feedRepository.findById(feedId);
        if (feed.isEmpty()) {
            throw new NotFoundException(String.format("존재하지 않는 피드 (%s) 입니다", feedId), NOT_FOUND_FEED_EXCEPTION);
        }

        Room room = feed.get().getRoom();

        // 피드 작성자가 방에 있을 때 피드 작성자가 아니면 삭제 불가능
        Optional<Participate> participating = participateRepository.findByRoomAndUser(room, feed.get().getUser());
        if (participating.isPresent() && feed.get().getUser().getId() != user.getId()) {
            throw new ForbiddenException(String.format("유저 (%s) 는 피드 (%s) 의 작성자가 아닙니다.", user.getId(), feedId), FORBIDDEN_FEED_OWNER_EXCEPTION);
        }

        ArrayList<FeedImage> feedImages = feedImageRepository.findAllByFeed(feed.get());
        for (FeedImage image : feedImages) {
            String date[] = image.getImageUrl().split(".com/");
            s3Service.deleteFile(date[1]);
            feedImageRepository.delete(image);
        }

        // 피드 삭제
        feedRepository.delete(feed.get());
    }
}
