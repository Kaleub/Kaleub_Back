package com.photory.service;

import com.photory.common.exception.model.ForbiddenException;
import com.photory.common.exception.model.NotFoundException;
import com.photory.controller.feed.dto.request.DeleteFeedRequestDto;
import com.photory.controller.feed.dto.request.ModifyFeedRequestDto;
import com.photory.controller.room.dto.request.CreateRoomRequestDto;
import com.photory.controller.room.dto.request.JoinRoomRequestDto;
import com.photory.domain.feed.Feed;
import com.photory.domain.feed.repository.FeedRepository;
import com.photory.domain.feedimage.FeedImage;
import com.photory.domain.feedimage.repository.FeedImageRepository;
import com.photory.domain.participate.repository.ParticipateRepository;
import com.photory.domain.room.Room;
import com.photory.domain.room.repository.RoomRepository;
import com.photory.domain.user.User;
import com.photory.domain.user.UserRole;
import com.photory.domain.user.repository.UserRepository;
import com.photory.service.feed.FeedService;
import com.photory.service.room.RoomService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class FeedServiceTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private FeedService feedService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ParticipateRepository participateRepository;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private FeedImageRepository feedImageRepository;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAllInBatch();
        roomRepository.deleteAllInBatch();
        participateRepository.deleteAllInBatch();
        feedRepository.deleteAllInBatch();
        feedImageRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("createFeedTest_성공")
    void createFeedTest_성공() throws IOException {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User feedOwner = userRepository.save(user1);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(feedOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(feedOwner);

        String fileName = "profile";
        String contentType = "png";
        String filePath = "src/test/resources/image/profile.png";
        MockMultipartFile mockMultipartFile = getMockMultipartFile(fileName, contentType, filePath);

        String userEmail = feedOwner.getEmail();
        Long roomId = room.get().getId();
        String title = "제목";
        String content = "내용";
        List<MultipartFile> images = new ArrayList<>() {
            {
                add(mockMultipartFile);
            }
        };

        //when
        feedService.createFeed(userEmail, images, roomId, title, content);

        //then
        List<Feed> feeds = feedRepository.findAll();
        List<FeedImage> feedImages = feedImageRepository.findAll();

        assertAll(
                () -> assertThat(feeds).hasSize(1),
                () -> assertThat(feedImages).hasSize(1),
                () -> assertEquals(feeds.get(0).getRoom().getId(), roomId),
                () -> assertEquals(feeds.get(0).getUser().getId(), feedOwner.getId()),
                () -> assertEquals(feeds.get(0).getTitle(), title),
                () -> assertEquals(feeds.get(0).getContent(), content),
                () -> assertThat(feedImages).hasSize(1),
                () -> assertEquals(feedImages.get(0).getFeed().getId(), feeds.get(0).getId())
        );

        //after
        DeleteFeedRequestDto deleteFeedRequestDto = DeleteFeedRequestDto.testBuilder()
                .feedId(feeds.get(0).getId())
                .build();
        feedService.deleteFeed(feedOwner.getEmail(), deleteFeedRequestDto);
    }

    @Test
    @DisplayName("createFeedTest_실패_참여하고_있는_방이_아닌_경우")
    void createFeedTest_실패_참여하고_있는_방이_아닌_경우() throws IOException {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User feedOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(feedOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(feedOwner);

        String fileName = "profile";
        String contentType = "png";
        String filePath = "src/test/resources/image/profile.png";
        MockMultipartFile mockMultipartFile = getMockMultipartFile(fileName, contentType, filePath);

        String userEmail = notOwner.getEmail();
        Long roomId = room.get().getId();
        String title = "제목";
        String content = "내용";
        List<MultipartFile> images = new ArrayList<>() {
            {
                add(mockMultipartFile);
            }
        };

        //when

        //then
        assertThrows(ForbiddenException.class, () -> feedService.createFeed(userEmail, images, roomId, title, content));
    }

    @Test
    @DisplayName("modifyFeedTest_성공")
    void modifyFeedTest_성공() throws IOException {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User feedOwner = userRepository.save(user1);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(feedOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(feedOwner);

        String fileName = "profile";
        String contentType = "png";
        String filePath = "src/test/resources/image/profile.png";
        MockMultipartFile mockMultipartFile = getMockMultipartFile(fileName, contentType, filePath);

        String userEmail = feedOwner.getEmail();
        Long roomId = room.get().getId();
        String title = "제목";
        String content = "내용";
        List<MultipartFile> images = new ArrayList<>() {
            {
                add(mockMultipartFile);
            }
        };

        feedService.createFeed(userEmail, images, roomId, title, content);

        List<Feed> feeds = feedRepository.findAll();

        ModifyFeedRequestDto modifyFeedRequestDto = ModifyFeedRequestDto.testBuilder()
                .feedId(feeds.get(0).getId())
                .title("제목 수정")
                .content("내용 수정")
                .build();
        //when
        feedService.modifyFeed(userEmail, modifyFeedRequestDto);

        //then
        Optional<Feed> feed = feedRepository.findById(1L);

        assertAll(
                () -> assertTrue(feed.isPresent()),
                () -> assertEquals(feed.get().getRoom().getId(), roomId),
                () -> assertEquals(feed.get().getUser().getId(), feedOwner.getId()),
                () -> assertEquals(feed.get().getTitle(), "제목 수정"),
                () -> assertEquals(feed.get().getContent(), "내용 수정")
        );

        //after
        DeleteFeedRequestDto deleteFeedRequestDto = DeleteFeedRequestDto.testBuilder()
                .feedId(feeds.get(0).getId())
                .build();
        feedService.deleteFeed(feedOwner.getEmail(), deleteFeedRequestDto);
    }

    @Test
    @DisplayName("modifyFeedTest_실패_피드가_존재하지_않는_경우")
    void modifyFeedTest_실패_피드가_존재하지_않는_경우() throws IOException {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User feedOwner = userRepository.save(user1);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(feedOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(feedOwner);

        String fileName = "profile";
        String contentType = "png";
        String filePath = "src/test/resources/image/profile.png";
        MockMultipartFile mockMultipartFile = getMockMultipartFile(fileName, contentType, filePath);

        String userEmail = feedOwner.getEmail();
        Long roomId = room.get().getId();
        String title = "제목";
        String content = "내용";
        List<MultipartFile> images = new ArrayList<>() {
            {
                add(mockMultipartFile);
            }
        };

        feedService.createFeed(userEmail, images, roomId, title, content);

        List<Feed> feeds = feedRepository.findAll();

        ModifyFeedRequestDto modifyFeedRequestDto = ModifyFeedRequestDto.testBuilder()
                .feedId(100L)
                .title("제목 수정")
                .content("내용 수정")
                .build();
        //when

        //then
        assertThrows(NotFoundException.class, () -> feedService.modifyFeed(userEmail, modifyFeedRequestDto));

        //after
        DeleteFeedRequestDto deleteFeedRequestDto = DeleteFeedRequestDto.testBuilder()
                .feedId(feeds.get(0).getId())
                .build();
        feedService.deleteFeed(feedOwner.getEmail(), deleteFeedRequestDto);
    }

    @Test
    @DisplayName("modifyFeedTest_실패_피드_작성자가_아닌_경우")
    void modifyFeedTest_실패_피드_작성자가_아닌_경우() throws IOException {
        //given
        User user1 = User.of("user1@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User user2 = User.of("user2@gmail.com", "password1", "닉네임", null, UserRole.ROLE_USER);
        User feedOwner = userRepository.save(user1);
        User notOwner = userRepository.save(user2);

        CreateRoomRequestDto createRoomRequestDto = CreateRoomRequestDto.testBuilder()
                .title("room")
                .password("password1")
                .build();
        roomService.createRoom(feedOwner.getEmail(), createRoomRequestDto);
        Optional<Room> room = roomRepository.findByOwnerUser(feedOwner);

        JoinRoomRequestDto joinRoomRequestDto = JoinRoomRequestDto.testBuilder()
                .code(room.get().getCode())
                .password("password1")
                .build();

        roomService.joinRoom(notOwner.getEmail(), joinRoomRequestDto);

        String fileName = "profile";
        String contentType = "png";
        String filePath = "src/test/resources/image/profile.png";
        MockMultipartFile mockMultipartFile = getMockMultipartFile(fileName, contentType, filePath);

        String userEmail = feedOwner.getEmail();
        Long roomId = room.get().getId();
        String title = "제목";
        String content = "내용";
        List<MultipartFile> images = new ArrayList<>() {
            {
                add(mockMultipartFile);
            }
        };

        feedService.createFeed(userEmail, images, roomId, title, content);

        List<Feed> feeds = feedRepository.findAll();

        ModifyFeedRequestDto modifyFeedRequestDto = ModifyFeedRequestDto.testBuilder()
                .feedId(feeds.get(0).getId())
                .title("제목 수정")
                .content("내용 수정")
                .build();
        //when

        //then
        assertThrows(ForbiddenException.class, () -> feedService.modifyFeed(notOwner.getEmail(), modifyFeedRequestDto));

        //after
        DeleteFeedRequestDto deleteFeedRequestDto = DeleteFeedRequestDto.testBuilder()
                .feedId(feeds.get(0).getId())
                .build();
        feedService.deleteFeed(feedOwner.getEmail(), deleteFeedRequestDto);
    }

    private MockMultipartFile getMockMultipartFile(String fileName, String contentType, String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(path));
        return new MockMultipartFile(fileName, fileName + "." + contentType, contentType, fileInputStream);
    }
}
