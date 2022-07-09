package com.photory.controller.room.dto.response;

import com.photory.common.dto.AuditingTimeResponse;
import com.photory.domain.room.Room;
import lombok.*;

import java.util.ArrayList;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetRoomResponse extends AuditingTimeResponse {

    private String code;
    private String ownerEmail;
    private String password;
    private int participantsCount;
    private boolean status;
    private ArrayList<Long> userIds;

    @Builder
    public GetRoomResponse(String code, String ownerEmail, String password, int participantsCount, boolean status, ArrayList<Long> userIds) {
        this.code = code;
        this.ownerEmail = ownerEmail;
        this.password = password;
        this.participantsCount = participantsCount;
        this.status = status;
        this.userIds = userIds;
    }

    public static GetRoomResponse of(Room room, ArrayList<Long> userIds) {
        GetRoomResponse response = GetRoomResponse.builder()
                .code(room.getCode())
                .ownerEmail(room.getOwnerUser().getEmail())
                .password(room.getPassword())
                .participantsCount(room.getParticipantsCount())
                .status(room.getStatus())
                .userIds(userIds)
                .build();
        response.setBaseTime(room);
        return response;
    }
}
