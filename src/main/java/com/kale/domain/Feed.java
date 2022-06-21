package com.kale.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false, length = 20)
    private String title;

    @Column(nullable = false, length = 100)
    private String content;

    @Builder
    public Feed(Room room, User user, String title, String content) {
        this.room = room;
        this.user = user;
        this.title = title;
        this.content = content;
    }
}
