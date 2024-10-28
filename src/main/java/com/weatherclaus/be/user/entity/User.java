package com.weatherclaus.be.user.entity;

import com.weatherclaus.be.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    // 아이디(중복검사, 변경불가)
    @Column(unique = true, nullable = false)
    private String username;

    // 이메일
    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String nickname;

    // 권한 필드 ("ROLE_ADMIN","ROLE_USER") -> ROLE_ 접두사 필수 !
    @Enumerated(EnumType.STRING)
    private Role role;

    // 비밀번호
    @Column(nullable = false)
    private String password;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column
    private boolean isActive = true;

    @Builder
    public User(String username, String email, Role role, String password, String imageUrl,String nickname) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.password = password;
        this.imageUrl = imageUrl;
        this.nickname = nickname;

    }

    public void changeImageUrl(String url){
        this.imageUrl = url;
    }

    public void changePassword(String password){
        this.password = password;
    }
}