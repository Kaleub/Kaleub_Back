package com.photory.dto.response.room;

import com.photory.domain.Room;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateRoomResDto {

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
    public CreateRoomResDto(Long id, String code, String ownerEmail, String title, String password, int participantsCount, Boolean status, long createdTimeInterval, long modifiedTimeInterval) {
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

    public static CreateRoomResDto of(Room room, long createdTimeInterval, long modifiedTimeInterval) {
        CreateRoomResDto response = CreateRoomResDto.builder()
                .id(room.getId())
                .code(room.getCode())
                .ownerEmail(room.getOwnerUser().getEmail())
                .title(room.getTitle())
                .password(room.getPassword())
                .participantsCount(room.getParticipantsCount())
                .status(room.getStatus())
                .createdTimeInterval(createdTimeInterval)
                .modifiedTimeInterval(modifiedTimeInterval)
                .build();
        return response;
    }
}
