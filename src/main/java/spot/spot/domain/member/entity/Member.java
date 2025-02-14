package spot.spot.domain.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spot.spot.domain.member.OAuthProvider;

import java.time.LocalDateTime;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="Members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String nickname;

    private double lat; //위도
    private double lng; //경도

    private String account;
    private int point;
    private LocalDateTime deletedAt; //삭제일자




    private OAuthProvider oAuthProvider; //소셜로그인

    public Member(String email,String nickname,OAuthProvider oAuthProvider){
        this.email = email;
        this.nickname = nickname;
        this.oAuthProvider = oAuthProvider;
    }



}

