package com.kale.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 20)
    private String ownerEmail;

    @Column(nullable = false, length = 20)
    private String title;

    @Column(nullable = false, length = 20)
    private String password;

    @Builder
    public Room(String code, String ownerEmail, String title, String password) {
        this.code = code;
        this.ownerEmail = ownerEmail;
        this.title = title;
        this.password = password;
    }
}
