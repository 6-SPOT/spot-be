package spot.spot.domain.member.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AbilityType {
    // Hard Skills
    CLEANING("청소"),
    COOKING("요리"),
    DRIVING("운전"),
    CHILD_CARE("아이 돌봄"),
    PET_CARE("반려동물 돌봄"),
    HANDY_MAN("간단한 수리 & 허드렛일 가능 가능"),
    TRANSLATION("번역 / 통역"),
    PHOTOGRAPHY("사진 / 영상 촬영"),
    TYPING("빠른 타이핑"),

    // Soft Skills
    PUNCTUALITY("시간 엄수"),
    TEAM_WORK("팀워크"),
    FAST_LEARNING("빠른 학습 능력"),
    COMMUNICATION("의사소통 능력"),
    RESPONSIBILITY("책임감"),
    FRIENDLY("친절함"),
    MULTI_TASKING("멀티태스킹 가능"),
    ATTENTION_TO_DETAIL("꼼꼼함"),

    // Certifications
    DRIVER_LICENSE("운전면허증 보유"),
    FOOD_SAFETY_CERT("식품 위생 관련 자격증"),
    CHILD_CARE_CERT("보육 관련 자격증"),
    PET_CARE_CERT("반려동물 돌봄 관련 자격증"),
    SECURITY_CERT("보안 관련 자격증"),;

    private final String displayName;

}
