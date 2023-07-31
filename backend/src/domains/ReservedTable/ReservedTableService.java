package domains.ReservedTable;

import domains.reservedTableCustomer.ReservedTableCustomer;
import domains.reservedTableCustomer.ReservedTableCustomerRepository;
import domains.table.Table;
import utils.Constants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

public class ReservedTableService {
    private final ReservedTableRepository reservedTableRepository;
    private final ReservedTableCustomerRepository reservedTableCustomerRepository;

    public ReservedTableService(ReservedTableRepository reservedTableRepository, ReservedTableCustomerRepository reservedTableCustomerRepository) {
        this.reservedTableRepository = reservedTableRepository;
        this.reservedTableCustomerRepository = reservedTableCustomerRepository;
    }

    public ReservedTable findById(int id) {
        return reservedTableRepository.findById(id).orElseThrow(() -> new NoSuchElementException(Constants.RESERVED_TABLE_NOT_FOUND));
    }

    public List<ReservedTable> findAll() {
        return reservedTableRepository.findAll();
    }

    public ReservedTable save(ReservedTable entity) {
        var reservedTable = reservedTableRepository.save(entity);

        entity.getCustomers()
                .forEach(customer -> reservedTable.getCustomers().add(
                        reservedTableCustomerRepository.save(new ReservedTableCustomer(reservedTable.getId(), customer)).getCustomer())
                );

        return reservedTable;
    }

    public void update(ReservedTable entity) {
        reservedTableRepository.update(entity);
    }

    public void delete(int entityId) {
        reservedTableRepository.delete(findById(entityId));
    }

    public List<ReservedTable> findByCustomer(int customerId) {
        return reservedTableRepository.findByCustomer(customerId);
    }

    public boolean existsByCustomerAndDateTime(int customerId, LocalDateTime dateTime) {
        return !reservedTableRepository.findAllByCustomerAndDateTime(customerId, dateTime).isEmpty();
    }

    public ReservedTable findByCustomerAndDatetime(int customerId, LocalDateTime dateTime) {
        return reservedTableRepository.findAllByCustomerAndDateTime(customerId, dateTime).stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(Constants.RESERVED_TABLE_NOT_FOUND));
    }

    public List<ReservedTable> findByTable(int tableId) {
        return reservedTableRepository.findByTable(tableId);
    }

    public boolean isReservedAtValid(Reservation reservedAt) {
        return reservedAt.start().isBefore(reservedAt.end()) && reservedAt.start().isAfter(LocalDateTime.now());
    }

    public boolean isOverlapping(Table table, Reservation reservedAt) {
        return findByTable(table.getId()).stream().anyMatch(reservedTable -> {
            var reservedAtStart = reservedAt.start();
            var reservedAtEnd = reservedAt.end();
            var reservedTableStart = reservedTable.getReservedAt().start();
            var reservedTableEnd = reservedTable.getReservedAt().end();

            return reservedAtStart.isBefore(reservedTableEnd) && reservedAtEnd.isAfter(reservedTableStart);
        });
    }

    public ReservedTable reserveTable(ReservedTable reservedTable) {
        // commented for testing purposes
//        if (!isReservedAtValid(reservedTable.getReservedAt())) {
//            throw new IllegalArgumentException("Horário de reserva inválido");
//        }

        if (isOverlapping(reservedTable.getTable(), reservedTable.getReservedAt())) {
            throw new IllegalArgumentException(Constants.RESERVED_TABLE_OVERLAPPING);
        }

        for (var customer : reservedTable.getCustomers()) {
            if (!customer.isActive()) {
                throw new IllegalStateException(Constants.INACTIVE_CUSTOMER);
            }
        }

        var numberOfPeople = reservedTable.getCustomers().size();
        if (numberOfPeople > reservedTable.getTable().getMaxCapacity()) {
            throw new IllegalArgumentException(Constants.RESERVED_TABLE_MAX_CAPACITY);
        }

        reservedTable.setNumberOfPeople(numberOfPeople);

        return save(reservedTable);
    }
}
