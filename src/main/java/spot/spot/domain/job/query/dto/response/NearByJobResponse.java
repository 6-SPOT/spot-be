package spot.spot.domain.job.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class NearByJobResponse {
    @Schema(description = "일의 고유 아이디")
    private long id;
    @Schema(description = "일의 제목")
    private String title;
    @Schema(description = "일의 내용")
    private String content;
    @Schema(description = "일의 프로필 사진")
    private String picture;
    @Schema(description = "위도")
    private double lat;
    @Schema(description = "경도")
    private double lng;
    @Schema(description = "돈")
    private int money;
    @Schema(description = "일과 현 사용자가 보낸 위치간의 거리 차이(km 단위)")
    private double dist;
    @Schema(description = "카카오페이 결제 번호")
    private String tid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NearByJobResponse that = (NearByJobResponse) o;
        return id == that.id &&
            Double.compare(that.lat, lat) == 0 &&
            Double.compare(that.lng, lng) == 0 &&
            money == that.money &&
            Double.compare(that.dist, dist) == 0 &&
            Objects.equals(title, that.title) &&
            Objects.equals(content, that.content) &&
            Objects.equals(picture, that.picture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, picture, lat, lng, money, dist);
    }

}
