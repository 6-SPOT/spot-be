package spot.spot.global.auditing.entitiy;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE #{h-table} SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Filter(name = "deletedFilter", condition = "deleted_at IS NULL")
@Getter
public class CreatedAndDeletedAndUpdated {

    @CreatedDate
    @Column(updatable = false, nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(nullable = true, name = "deleted_at")
    private LocalDateTime deletedAt;

    @LastModifiedDate
    @Column(updatable = true, nullable = true, name = "updated_at")
    private LocalDateTime updatedAt;
}
