package com.kale.controller;

import com.kale.dto.ResponseDto;
import com.kale.dto.request.auth.LoginUserReqDto;
import com.kale.dto.request.room.CreateRoomReqDto;
import com.kale.model.Room;
import com.kale.model.User;
import com.kale.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<ResponseDto> createRoom(
            @RequestBody CreateRoomReqDto createRoomReqDto,
            HttpServletResponse response
            ) {

        String userEmail = response.getHeader("user");

        Room room = roomService.createRoom(userEmail, createRoomReqDto.getTitle(), createRoomReqDto.getPassword());

        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseDto.builder()
                        .status(200)
                        .message("방 생성 성공")
                        .data(room)
                        .build()
        );
    }
}
