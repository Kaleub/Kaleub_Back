package com.photory.controller.room;

import com.photory.config.resolver.UserEmail;
import com.photory.controller.room.dto.request.*;
import com.photory.common.dto.ResponseDto;
import com.photory.controller.room.dto.response.CreateRoomResponse;
import com.photory.controller.room.dto.response.GetRoomsResponse;
import com.photory.controller.room.dto.response.JoinRoomResponse;
import com.photory.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/room")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<ResponseDto> createRoom(
            @RequestBody @Valid CreateRoomRequestDto createRoomRequestDto,
            @UserEmail String userEmail
    ) {
        System.out.println(userEmail);
        CreateRoomResponse createRoomResponse = roomService.createRoom(userEmail, createRoomRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("방 생성 성공")
                        .data(createRoomResponse)
                        .build()
        );
    }

    @PostMapping("/participate")
    public ResponseEntity<ResponseDto> joinRoom(
            @RequestBody JoinRoomRequestDto joinRoomRequestDto,
            @UserEmail String userEmail
    ) {
        JoinRoomResponse joinRoomResponse = roomService.joinRoom(userEmail, joinRoomRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("방 참가 성공")
                        .data(joinRoomResponse)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ResponseDto> getRooms(
            @UserEmail String userEmail
    ) {
        ArrayList<GetRoomsResponse> getRoomsResponses = roomService.getRooms(userEmail);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("참여중인 방 목록 조회 성공")
                        .data(getRoomsResponses)
                        .build()
        );
    }

    @DeleteMapping("/participate")
    public ResponseEntity<ResponseDto> leaveRoom(
            @RequestBody LeaveRoomRequestDto leaveRoomRequestDto,
            @UserEmail String userEmail
    ) {
        roomService.leaveRoom(userEmail, leaveRoomRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("방 나가기 성공")
                        .data(null)
                        .build()
        );
    }

    @PutMapping("/disable")
    public ResponseEntity<ResponseDto> disableRoom(
            @RequestBody DisableRoomRequestDto disableRoomRequestDto,
            @UserEmail String userEmail
    ) {
        roomService.disableRoom(userEmail, disableRoomRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("방 비활성화 성공")
                        .data(null)
                        .build()
        );
    }

    @DeleteMapping("/participate/force")
    public ResponseEntity<ResponseDto> deleteUserForce(
            @RequestBody DeleteUserForceRequestDto deleteUserForceRequestDto,
            @UserEmail String userEmail
    ) {
        roomService.deleteUserForce(userEmail, deleteUserForceRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("사용자 강퇴 성공")
                        .data(null)
                        .build()
        );
    }

    @PutMapping("/password")
    public ResponseEntity<ResponseDto> modifyRoomPassword(
            @RequestBody @Valid ModifyRoomPasswordRequestDto modifyRoomPasswordRequestDto,
            @UserEmail String userEmail
    ) {
        roomService.modifyRoomPassword(userEmail, modifyRoomPasswordRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("방 비밀번호 변경 성공")
                        .data(null)
                        .build()
        );
    }

    @PutMapping("/owner")
    public ResponseEntity<ResponseDto> delegateOwner(
            @RequestBody @Valid DelegateOwnerRequestDto delegateOwnerRequestDto,
            @UserEmail String userEmail
    ) {
        roomService.delegateOwner(userEmail, delegateOwnerRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("방장 위임 성공")
                        .data(null)
                        .build()
        );
    }
}
