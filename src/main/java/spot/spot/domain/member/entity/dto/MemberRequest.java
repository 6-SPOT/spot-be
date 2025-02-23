package spot.spot.domain.member.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequest {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class register{
        private String nickname;
        private String email;
        private String img;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class modify {
        private String nickname;
        private String email;
        private String img;
        private String phone;
        private double lat; //위도
        private double lng; //경도
    }
}
