package com.photory.controller;

import com.photory.config.resolver.UserEmail;
import com.photory.dto.ResponseDto;
import com.photory.dto.request.room.*;
import com.photory.dto.response.room.CreateRoomResDto;
import com.photory.dto.response.room.GetRoomsResDto;
import com.photory.dto.response.room.JoinRoomResDto;
import com.photory.service.RoomService;
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
            @RequestBody @Valid CreateRoomReqDto createRoomReqDto,
            @UserEmail String userEmail
    ) {
        System.out.println(userEmail);
        CreateRoomResDto createRoomResDto = roomService.createRoom(userEmail, createRoomReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("방 생성 성공")
                        .data(createRoomResDto)
                        .build()
        );
    }

    @PostMapping("/participate")
    public ResponseEntity<ResponseDto> joinRoom(
            @RequestBody JoinRoomReqDto joinRoomReqDto,
            @UserEmail String userEmail
    ) {
        JoinRoomResDto joinRoomResDto = roomService.joinRoom(userEmail, joinRoomReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("방 참가 성공")
                        .data(joinRoomResDto)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ResponseDto> getRooms(
            @UserEmail String userEmail
    ) {
        ArrayList<GetRoomsResDto> getRoomsResDtos = roomService.getRooms(userEmail);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("참여중인 방 목록 조회 성공")
                        .data(getRoomsResDtos)
                        .build()
        );
    }

    @DeleteMapping("/participate")
    public ResponseEntity<ResponseDto> leaveRoom(
            @RequestBody LeaveRoomReqDto leaveRoomReqDto,
            @UserEmail String userEmail
    ) {
        roomService.leaveRoom(userEmail, leaveRoomReqDto);
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
            @RequestBody DisableRoomReqDto disableRoomReqDto,
            @UserEmail String userEmail
    ) {
        roomService.disableRoom(userEmail, disableRoomReqDto);
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
            @RequestBody DeleteUserForceReqDto deleteUserForceReqDto,
            @UserEmail String userEmail
    ) {
        roomService.deleteUserForce(userEmail, deleteUserForceReqDto);
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
            @RequestBody @Valid ModifyRoomPasswordReqDto modifyRoomPasswordReqDto,
            @UserEmail String userEmail
    ) {
        roomService.modifyRoomPassword(userEmail, modifyRoomPasswordReqDto);
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
            @RequestBody @Valid DelegateOwnerReqDto delegateOwnerReqDto,
            @UserEmail String userEmail
    ) {
        roomService.delegateOwner(userEmail, delegateOwnerReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("방장 위임 성공")
                        .data(null)
                        .build()
        );
    }
}
