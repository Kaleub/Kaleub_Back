package com.kale.service;

import com.kale.domain.*;
import com.kale.exception.NotInRoomException;
import com.kale.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
}
