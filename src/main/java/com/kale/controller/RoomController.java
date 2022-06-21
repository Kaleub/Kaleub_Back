package com.kale.controller;

import com.kale.dto.ResponseDto;
import com.kale.dto.request.room.*;
import com.kale.dto.response.room.CreateRoomResDto;
import com.kale.dto.response.room.GetRoomsResDto;
import com.kale.dto.response.room.JoinRoomResDto;
import com.kale.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
            HttpServletResponse response
    ) {
        String userEmail = response.getHeader("user");
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
            HttpServletResponse response
    ) {
        String userEmail = response.getHeader("user");
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
            HttpServletResponse response
    ) {
        String userEmail = response.getHeader("user");
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
            HttpServletResponse response
    ) {
        String userEmail = response.getHeader("user");
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
            HttpServletResponse response
    ) {
        String userEmail = response.getHeader("user");
        roomService.disableRoom(userEmail, disableRoomReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("방 삭제 성공")
                        .data(null)
                        .build()
        );
    }

    @DeleteMapping("/participate/force")
    public ResponseEntity<ResponseDto> deleteUserForce(
            @RequestBody DeleteUserForceReqDto deleteUserForceReqDto,
            HttpServletResponse response
    ) {
        String userEmail = response.getHeader("user");
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
            HttpServletResponse response
    ) {
        String userEmail = response.getHeader("user");
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
            HttpServletResponse response
    ) {
        String userEmail = response.getHeader("user");
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
