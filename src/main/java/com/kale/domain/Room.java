package com.kale.domain;

import lombok.*;

import javax.persistence.*;

@Table
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Setter
    @ManyToOne
    @JoinColumn(name="USER_ID")
    private User ownerUser;

    @Column(nullable = false, length = 20)
    private String title;

    @Setter
    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false)
    private int participantsCount;

    @Setter
    @Column(nullable = false)
    private Boolean status;

    @Builder
    public Room(String code, User ownerUser, String title, String password, int participantsCount, Boolean status) {
        this.code = code;
        this.ownerUser = ownerUser;
        this.title = title;
        this.password = password;
        this.participantsCount = participantsCount;
        this.status = status;
    }
}
