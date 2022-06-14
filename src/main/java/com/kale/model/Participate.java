package com.kale.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participate extends BaseEntity {

    @ManyToOne
    @JoinColumn(name="ROOM_ID")
    private Room room;

    @Column(nullable = false, length = 20)
    private String userEmail;

    @Builder
    public Participate(Room room, String userEmail) {
        this.room = room;
        this.userEmail = userEmail;
    }
}
