package interfaces;

import java.time.LocalDateTime;

public interface Auditable {
    LocalDateTime getCreatedAt();

    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();

    void setUpdatedAt(LocalDateTime updatedAt);

    Integer getCreatedBy();

    void setCreatedBy(Integer createdBy);

    Integer getUpdatedBy();

    void setUpdatedBy(Integer updatedBy);
}
