package domains.ReservedTable;

import java.time.LocalDateTime;

public record Reservation(LocalDateTime start, LocalDateTime end) {
}
