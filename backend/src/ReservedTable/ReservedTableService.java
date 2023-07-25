package ReservedTable;

import table.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

public class ReservedTableService {
    private final ReservedTableDAO reservedTableDAO;

    public ReservedTableService(ReservedTableDAO reservedTableDAO) {
        this.reservedTableDAO = reservedTableDAO;
    }

    public ReservedTable findById(int id) {
        return reservedTableDAO.findById(id).orElseThrow(() -> new NoSuchElementException("Mesa não encontrada"));
    }

    public List<ReservedTable> findAll() {
        return reservedTableDAO.findAll();
    }

    public ReservedTable save(ReservedTable entity) {
        return reservedTableDAO.save(entity);
    }

    public void update(ReservedTable entity) {
        reservedTableDAO.update(entity);
    }

    public void delete(ReservedTable entity) {
        reservedTableDAO.delete(entity);
    }

    public List<ReservedTable> findByCustomer(int customerId) {
        return reservedTableDAO.findByCustomer(customerId);
    }

    public List<ReservedTable> findByTable(int tableId) {
        return reservedTableDAO.findByTable(tableId);
    }

    public boolean isReservedAtValid(Reservation reservedAt) {
        return reservedAt.getStart().isBefore(reservedAt.getEnd()) && reservedAt.getStart().isAfter(LocalDateTime.now());
    }

    public boolean isOverlapping(Table table, Reservation reservedAt) {
        return findByTable(table.getId()).stream().anyMatch(reservedTable -> {
            var reservedAtStart = reservedAt.getStart();
            var reservedAtEnd = reservedAt.getEnd();
            var reservedTableStart = reservedTable.getReservedAt().getStart();
            var reservedTableEnd = reservedTable.getReservedAt().getEnd();

            return reservedAtStart.isBefore(reservedTableEnd) && reservedAtEnd.isAfter(reservedTableStart);
        });
    }

    public ReservedTable reserveTable(Table table, ReservedTable reservedTable) {
        if (!isReservedAtValid(reservedTable.getReservedAt())) {
            throw new IllegalArgumentException("Horário de reserva inválido");
        }

        if (isOverlapping(table, reservedTable.getReservedAt())) {
            throw new IllegalArgumentException("Mesa não está disponível no horário");
        }

        var numberOfPeople = reservedTable.getCustomers().size();
        if (numberOfPeople > table.getMaxCapacity()) {
            throw new IllegalArgumentException("Mesa não comporta o número de pessoas");
        }

        return save(
                new ReservedTable(
                        reservedTable.getCustomers(),
                        new Reservation(reservedTable.getReservedAt().getStart(), reservedTable.getReservedAt().getEnd()),
                        table,
                        numberOfPeople,
                        reservedTable.getCreatedBy()
                )
        );
    }
}
