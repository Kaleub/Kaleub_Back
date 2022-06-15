package com.kale.model;

import lombok.*;

import javax.persistence.*;

@Table
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @ManyToOne
    @JoinColumn(name="USER_ID")
    private User ownerUser;

    @Column(nullable = false, length = 20)
    private String title;

    @Setter
    @Column(nullable = false, length = 100)
    private String password;

    @Builder
    public Room(String code, User ownerUser, String title, String password) {
        this.code = code;
        this.ownerUser = ownerUser;
        this.title = title;
        this.password = password;
    }
}
