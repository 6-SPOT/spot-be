package spot.spot.domain.member.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Schema(description = "사용자가 보유한 능력 유형 (AbilityType)")
public enum AbilityType {

    // Hard Skills
    @Schema(description = "청소") CLEANING("청소"),
    @Schema(description = "요리") COOKING("요리"),
    @Schema(description = "운전") DRIVING("운전"),
    @Schema(description = "아이 돌봄") CHILD_CARE("아이 돌봄"),
    @Schema(description = "반려동물 돌봄") PET_CARE("반려동물 돌봄"),
    @Schema(description = "간단한 수리 & 허드렛일 가능") HANDY_MAN("간단한 수리 & 허드렛일 가능"),
    @Schema(description = "번역 / 통역") TRANSLATION("번역 / 통역"),
    @Schema(description = "사진 / 영상 촬영") PHOTOGRAPHY("사진 / 영상 촬영"),
    @Schema(description = "빠른 타이핑") TYPING("빠른 타이핑"),

    // Soft Skills
    @Schema(description = "시간 엄수") PUNCTUALITY("시간 엄수"),
    @Schema(description = "팀워크") TEAM_WORK("팀워크"),
    @Schema(description = "빠른 학습 능력") FAST_LEARNING("빠른 학습 능력"),
    @Schema(description = "의사소통 능력") COMMUNICATION("의사소통 능력"),
    @Schema(description = "책임감") RESPONSIBILITY("책임감"),
    @Schema(description = "친절함") FRIENDLY("친절함"),
    @Schema(description = "멀티태스킹 가능") MULTI_TASKING("멀티태스킹 가능"),
    @Schema(description = "꼼꼼함") ATTENTION_TO_DETAIL("꼼꼼함"),

    // Certifications
    @Schema(description = "운전면허증 보유") DRIVER_LICENSE("운전면허증 보유"),
    @Schema(description = "식품 위생 관련 자격증") FOOD_SAFETY_CERT("식품 위생 관련 자격증"),
    @Schema(description = "보육 관련 자격증") CHILD_CARE_CERT("보육 관련 자격증"),
    @Schema(description = "반려동물 돌봄 관련 자격증") PET_CARE_CERT("반려동물 돌봄 관련 자격증"),
    @Schema(description = "보안 관련 자격증") SECURITY_CERT("보안 관련 자격증");

    private final String displayName;

}
