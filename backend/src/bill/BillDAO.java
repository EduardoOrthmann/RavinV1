package bill;

import interfaces.Crud;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class BillDAO implements Crud<Bill> {
    private final List<Bill> billList;

    public BillDAO() {
        this.billList = new ArrayList<>();
    }

    @Override
    public Optional<Bill> findById(int id) {
        return billList.stream()
                .filter(bill -> bill.getId() == id)
                .findFirst();
    }

    @Override
    public List<Bill> findAll() {
        return this.billList;
    }

    @Override
    public Bill save(Bill entity) {
        this.billList.add(entity);
        return entity;
    }

    @Override
    public void update(Bill entity) {
        var command = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Comanda não encontrada"));
        this.billList.set(billList.indexOf(command), entity);
    }

    @Override
    public void delete(Bill entity) {
        var command = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Comanda não encontrada"));
        this.billList.remove(command);
    }

    public Optional<Bill> findByOrderId(int orderId) {
        return this.billList.stream()
                .filter(bill -> bill.getOrders().stream().anyMatch(order -> order.getId() == orderId))
                .findFirst();
    }

    public boolean existsByTableAndIsPaid(int tableId, boolean isPaid) {
        return this.billList.stream()
                .anyMatch(bill -> bill.getTable().getId() == tableId && bill.isPaid() == isPaid);
    }
}
