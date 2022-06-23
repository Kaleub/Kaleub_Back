package com.photory.controller.room.dto.response;

import com.photory.common.dto.AuditingTimeResponse;
import com.photory.domain.room.Room;
import com.photory.common.util.DateUtil;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinRoomResponse extends AuditingTimeResponse {

    private Long id;
    private String code;
    private String ownerEmail;
    private String title;
    private String password;
    private int participantsCount;
    private Boolean status;

    @Builder
    public JoinRoomResponse(Long id, String code, String ownerEmail, String title, String password, int participantsCount, Boolean status) {
        this.id = id;
        this.code = code;
        this.ownerEmail = ownerEmail;
        this.title = title;
        this.password = password;
        this.participantsCount = participantsCount;
        this.status = status;
    }

    public static JoinRoomResponse of(Room room) {
        JoinRoomResponse response = JoinRoomResponse.builder()
                .id(room.getId())
                .code(room.getCode())
                .ownerEmail(room.getOwnerUser().getEmail())
                .title(room.getTitle())
                .password(room.getPassword())
                .participantsCount(room.getParticipantsCount())
                .status(room.getStatus())
                .build();
        response.setBaseTime(room);
        return response;
    }
}
