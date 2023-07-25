package ReservedTable;

import interfaces.Crud;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservedTableDAO implements Crud<ReservedTable> {
    private final List<ReservedTable> reservedTableList;

    public ReservedTableDAO() {
        this.reservedTableList = new ArrayList<>();
    }

    @Override
    public Optional<ReservedTable> findById(int id) {
        return reservedTableList.stream()
                .filter(reservedTable -> reservedTable.getId() == id)
                .findFirst();
    }

    @Override
    public List<ReservedTable> findAll() {
        return this.reservedTableList;
    }

    @Override
    public void delete(ReservedTable entity) {
        var reservedTable = findById(entity.getId()).orElseThrow(() -> new RuntimeException("Mesa reservada não encontrada"));
        this.reservedTableList.remove(reservedTable);
    }

    @Override
    public void update(ReservedTable entity) {
        var reservedTable = findById(entity.getId()).orElseThrow(() -> new RuntimeException("Mesa reservada não encontrada"));
        this.reservedTableList.set(reservedTableList.indexOf(reservedTable), entity);
    }

    @Override
    public ReservedTable save(ReservedTable entity) {
        this.reservedTableList.add(entity);
        return entity;
    }

    public List<ReservedTable> findByCustomer(int customerId) {
        return reservedTableList.stream()
                .filter(reservedTable -> reservedTable.getCustomers().stream().anyMatch(customer -> customer.getId() == customerId))
                .toList();
    }

    public List<ReservedTable> findByTable(int tableId) {
        return reservedTableList.stream()
                .filter(reservedTable -> reservedTable.getTable().getId() == tableId)
                .toList();
    }

    public Optional<ReservedTable> findByCustomerAndDateTime(int customerId, LocalDateTime dateTime) {
        return reservedTableList.stream()
                .filter(reservedTable -> reservedTable.getCustomers().stream().anyMatch(customer -> customer.getId() == customerId))
                .filter(reservedTable -> reservedTable.getReservedAt().getStart().isBefore(dateTime) && reservedTable.getReservedAt().getEnd().isAfter(dateTime))
                .findFirst();
    }
}
