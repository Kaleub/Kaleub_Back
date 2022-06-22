package com.photory.controller.room;

import com.photory.common.dto.ApiResponse;
import com.photory.config.resolver.UserEmail;
import com.photory.controller.room.dto.request.*;
import com.photory.controller.room.dto.response.CreateRoomResponse;
import com.photory.controller.room.dto.response.GetRoomsResponse;
import com.photory.controller.room.dto.response.JoinRoomResponse;
import com.photory.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/room")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ApiResponse<CreateRoomResponse> createRoom(@RequestBody @Valid CreateRoomRequestDto createRoomRequestDto, @UserEmail String userEmail) {
        CreateRoomResponse createRoomResponse = roomService.createRoom(userEmail, createRoomRequestDto);
        return ApiResponse.success(createRoomResponse);
    }

    @PostMapping("/participate")
    public ApiResponse<JoinRoomResponse> joinRoom(@RequestBody JoinRoomRequestDto joinRoomRequestDto, @UserEmail String userEmail) {
        JoinRoomResponse joinRoomResponse = roomService.joinRoom(userEmail, joinRoomRequestDto);
        return ApiResponse.success(joinRoomResponse);
    }

    @GetMapping
    public ApiResponse<ArrayList<GetRoomsResponse>> getRooms(@UserEmail String userEmail) {
        ArrayList<GetRoomsResponse> getRoomsResponses = roomService.getRooms(userEmail);
        return ApiResponse.success(getRoomsResponses);
    }

    @DeleteMapping("/participate")
    public ApiResponse<String> leaveRoom(@RequestBody LeaveRoomRequestDto leaveRoomRequestDto, @UserEmail String userEmail) {
        roomService.leaveRoom(userEmail, leaveRoomRequestDto);
        return ApiResponse.SUCCESS;
    }

    @PutMapping("/disable")
    public ApiResponse<String> disableRoom(@RequestBody DisableRoomRequestDto disableRoomRequestDto, @UserEmail String userEmail) {
        roomService.disableRoom(userEmail, disableRoomRequestDto);
        return ApiResponse.SUCCESS;
    }

    @DeleteMapping("/participate/force")
    public ApiResponse<String> deleteUserForce(@RequestBody DeleteUserForceRequestDto deleteUserForceRequestDto, @UserEmail String userEmail) {
        roomService.deleteUserForce(userEmail, deleteUserForceRequestDto);
        return ApiResponse.SUCCESS;
    }

    @PutMapping("/password")
    public ApiResponse<String> modifyRoomPassword(@RequestBody @Valid ModifyRoomPasswordRequestDto modifyRoomPasswordRequestDto, @UserEmail String userEmail) {
        roomService.modifyRoomPassword(userEmail, modifyRoomPasswordRequestDto);
        return ApiResponse.SUCCESS;
    }

    @PutMapping("/owner")
    public ApiResponse<String> delegateOwner(@RequestBody @Valid DelegateOwnerRequestDto delegateOwnerRequestDto, @UserEmail String userEmail) {
        roomService.delegateOwner(userEmail, delegateOwnerRequestDto);
        return ApiResponse.SUCCESS;
    }
}
