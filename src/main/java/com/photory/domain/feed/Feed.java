package com.photory.domain.feed;

import com.photory.domain.common.BaseEntity;
import com.photory.domain.user.User;
import com.photory.domain.room.Room;
import lombok.*;

import javax.persistence.*;

@Table
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="ROOM_ID")
    private Room room;

    @ManyToOne
    @JoinColumn(name="USER_ID")
    private User user;

    @Setter
    @Column(nullable = false, length = 20)
    private String title;

    @Setter
    @Column(nullable = false, length = 100)
    private String content;

    @Builder
    public Feed(Room room, User user, String title, String content) {
        this.room = room;
        this.user = user;
        this.title = title;
        this.content = content;
    }

    public static Feed of(Room room, User user, String title, String content) {
        return Feed.builder()
                .room(room)
                .user(user)
                .title(title)
                .content(content)
                .build();
    }
}
