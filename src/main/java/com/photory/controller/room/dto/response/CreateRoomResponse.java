package com.photory.controller.room.dto.response;

import com.photory.domain.room.Room;
import com.photory.common.util.DateUtil;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateRoomResponse {

    private Long id;
    private String code;
    private String ownerEmail;
    private String title;
    private String password;
    private int participantsCount;
    private Boolean status;
    private long createdTimeInterval;
    private long modifiedTimeInterval;

    @Builder
    public CreateRoomResponse(Long id, String code, String ownerEmail, String title, String password, int participantsCount, Boolean status, long createdTimeInterval, long modifiedTimeInterval) {
        this.id = id;
        this.code = code;
        this.ownerEmail = ownerEmail;
        this.title = title;
        this.password = password;
        this.participantsCount = participantsCount;
        this.status = status;
        this.createdTimeInterval = createdTimeInterval;
        this.modifiedTimeInterval = modifiedTimeInterval;
    }

    public static CreateRoomResponse of(Room room) {
        CreateRoomResponse response = CreateRoomResponse.builder()
                .id(room.getId())
                .code(room.getCode())
                .ownerEmail(room.getOwnerUser().getEmail())
                .title(room.getTitle())
                .password(room.getPassword())
                .participantsCount(room.getParticipantsCount())
                .status(room.getStatus())
                .createdTimeInterval(DateUtil.convertToTimeInterval(room.getCreatedDate()))
                .modifiedTimeInterval(DateUtil.convertToTimeInterval(room.getModifiedDate()))
                .build();
        return response;
    }
}
