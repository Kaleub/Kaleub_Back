package com.photory.controller.feed;

import com.photory.common.dto.ApiResponse;
import com.photory.config.resolver.UserEmail;
import com.photory.controller.feed.dto.request.DeleteFeedRequestDto;
import com.photory.controller.feed.dto.request.ModifyFeedRequestDto;
import com.photory.controller.feed.dto.response.GetFeedResponse;
import com.photory.controller.feed.dto.response.GetFeedsResponse;
import com.photory.controller.feed.dto.response.ModifyFeedResponse;
import com.photory.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
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
    public ApiResponse<String> createFeed(
            @RequestPart List<MultipartFile> images,
            @RequestParam Long roomId,
            @RequestParam @NotBlank(message = "제목을 입력해야합니다.") String title,
            @RequestParam @NotNull(message = "내용은 null 값일 수 없습니다.") String content,
            @UserEmail String userEmail
    ) {
        feedService.createFeed(userEmail, images, roomId, title, content);
        return ApiResponse.SUCCESS;
    }

    @GetMapping
    public ApiResponse<GetFeedsResponse> getFeeds(@RequestParam Long roomId,
                                                  @RequestParam int size,
                                                  @RequestParam Long lastFeedId,
                                                  @UserEmail String userEmail) {
        GetFeedsResponse response = feedService.getFeeds(userEmail, roomId, size, lastFeedId);
        return ApiResponse.success(response);
    }

    @GetMapping("/{feedId}")
    public ApiResponse<GetFeedResponse> getFeed(@PathVariable("feedId") @Valid Long feedId, @UserEmail String userEmail) {
        GetFeedResponse response = feedService.getFeed(userEmail, feedId);
        return ApiResponse.success(response);
    }

    @PutMapping
    public ApiResponse<ModifyFeedResponse> modifyFeed(@RequestBody @Valid ModifyFeedRequestDto request, @UserEmail String userEmail) {
        ModifyFeedResponse response = feedService.modifyFeed(userEmail, request);
        return ApiResponse.success(response);
    }

    @DeleteMapping
    public ApiResponse<String> deleteFeed(@RequestBody @Valid DeleteFeedRequestDto request, @UserEmail String userEmail) {
        feedService.deleteFeed(userEmail, request);
        return ApiResponse.SUCCESS;
    }
}
