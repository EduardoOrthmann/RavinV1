package interfaces;

import java.time.LocalDateTime;

public interface Auditable {
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    Integer getCreatedBy();
    Integer getUpdatedBy();
}
