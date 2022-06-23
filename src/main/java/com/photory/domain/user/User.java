package com.photory.domain.user;

import com.photory.domain.common.AuditingTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 10)
    private String nickname;

    @Column
    private String imageUrl;

    @Column(nullable = false, length = 20)
    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @Builder
    public User(String email, String password, String nickname, String imageUrl, UserRole role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.role = role;
    }

    public static User of(String email, String password, String nickname, String imageUrl, UserRole role) {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .imageUrl(imageUrl)
                .role(role)
                .build();
    }
}
