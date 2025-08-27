package spot.spot.global.auditing.entitiy;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

@Getter
@MappedSuperclass
@SQLDelete(sql = "UPDATE #{h-table} SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Filter(name = "deletedFilter", condition = "deleted_at IS NULL")   // 필터의 사용법 -> 수동으로 써야지 정의됨.
public abstract class Deleted {

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
