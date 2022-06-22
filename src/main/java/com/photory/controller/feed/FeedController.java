package com.photory.controller.feed;

import com.photory.config.resolver.UserEmail;
import com.photory.common.dto.ResponseDto;
import com.photory.controller.feed.dto.request.DeleteFeedReqDto;
import com.photory.controller.feed.dto.request.ModifyFeedReqDto;
import com.photory.controller.feed.dto.response.ModifyFeedResDto;
import com.photory.controller.feed.dto.response.GetFeedResDto;
import com.photory.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/feed")
public class FeedController {

    private final FeedService feedService;

    @PostMapping
    public ResponseEntity<ResponseDto> createFeed(
            @RequestPart List<MultipartFile> images,
            @RequestParam Long roomId,
            @RequestParam @NotBlank(message = "제목을 입력해야합니다.") String title,
            @RequestParam @NotNull(message = "내용은 null 값일 수 없습니다.") String content,
            @UserEmail String userEmail
    ) {
        feedService.createFeed(userEmail, images, roomId, title, content);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("피드 생성 성공")
                        .data(null)
                        .build()
        );
    }

    @GetMapping("/{feedId}")
    public ResponseEntity<ResponseDto> getFeed(
            @PathVariable("feedId") @Valid Long feedId,
            @UserEmail String userEmail
    ) {
        GetFeedResDto getFeedResDto = feedService.getFeed(userEmail, feedId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("피드 조회 성공")
                        .data(getFeedResDto)
                        .build()
        );
    }

    @PutMapping
    public ResponseEntity<ResponseDto> modifyFeed(
            @RequestBody @Valid ModifyFeedReqDto modifyFeedReqDto,
            @UserEmail String userEmail
    ) {
        ModifyFeedResDto modifyFeedResDto = feedService.modifyFeed(userEmail, modifyFeedReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("피드 수정 성공")
                        .data(modifyFeedResDto)
                        .build()
        );
    }

    @DeleteMapping
    public ResponseEntity<ResponseDto> deleteFeed(
            @RequestBody @Valid DeleteFeedReqDto deleteFeedReqDto,
            @UserEmail String userEmail
    ) {
        feedService.deleteFeed(userEmail, deleteFeedReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("피드 삭제 성공")
                        .data(null)
                        .build()
        );
    }
}
