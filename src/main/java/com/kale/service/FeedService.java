package com.kale.service;

import com.kale.domain.*;
import com.kale.dto.request.feed.ModifyFeedReqDto;
import com.kale.dto.response.feed.ModifyFeedResDto;
import com.kale.exception.NotFeedOwnerException;
import com.kale.exception.NotFoundFeedException;
import com.kale.exception.NotInRoomException;
import com.kale.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            throw new NotInRoomException();
        }

        Feed feed = Feed.builder()
                .room(room)
                .user(user)
                .title(title)
                .content(content)
                .build();

        Feed savedFeed = feedRepository.save(feed);

        List<String> fileUrlList = s3Service.uploadFile(images);
        fileUrlList.forEach(file -> {
            FeedImage feedImage = FeedImage.builder()
                    .feed(savedFeed)
                    .imageUrl(file)
                    .build();

            feedImageRepository.save(feedImage);
        });
    }

    public ModifyFeedResDto modifyFeed(String userEmail, ModifyFeedReqDto modifyFeedReqDto) {
        User user = FeedServiceUtils.findUserByEmail(userRepository, userEmail);
        Long feedId = modifyFeedReqDto.getFeedId();
        String title = modifyFeedReqDto.getTitle();
        String content = modifyFeedReqDto.getContent();

        Optional<Feed> feed = feedRepository.findById(feedId);
        if (feed.isEmpty()) {
            throw new NotFoundFeedException();
        }

        // 피드 작성자가 아니면 수정할 수 없음
        if (feed.get().getUser() != user) {
            throw new NotFeedOwnerException();
        }

        feed.get().setTitle(title);
        feed.get().setContent(content);

        Feed modified = feedRepository.save(feed.get());

        ArrayList<String> imageUrls = new ArrayList<>();
        ArrayList<FeedImage> feedImages = feedImageRepository.findAllByFeed(feed.get());
        feedImages.forEach(image -> {
            imageUrls.add(imageUrls.size(), image.getImageUrl());
        });

        ModifyFeedResDto modifyFeedResDto = ModifyFeedResDto.builder()
                .roomId(modified.getRoom().getId())
                .userId(modified.getUser().getId())
                .title(modified.getTitle())
                .content(modified.getContent())
                .imageUrls(imageUrls)
                .build();

        return modifyFeedResDto;
    }
}
