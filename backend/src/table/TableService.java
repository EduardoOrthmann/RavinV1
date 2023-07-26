package table;

import bill.BillService;
import customer.Customer;
import enums.TableStatus;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class TableService {
    private final TableDAO tableDAO;
    private final BillService billService;

    public TableService(TableDAO tableDAO, BillService billService) {
        this.tableDAO = tableDAO;
        this.billService = billService;
    }

    public Table findById(int id) {
        return tableDAO.findById(id).orElseThrow(() -> new NoSuchElementException("Mesa não encontrado"));
    }

    public List<Table> findAll() {
        return tableDAO.findAll();
    }

    public Table save(Table entity) {
        return tableDAO.save(entity);
    }

    public void update(Table entity) {
        tableDAO.update(entity);
    }

    public void delete(Table entity) {
        tableDAO.delete(entity);
    }

    public void occupyTable(Table table, Set<Customer> customers) {
        if (billService.existsByTableAndIsPaid(table.getId(), false)) {
            throw new IllegalStateException("Não é possível ocupar uma mesa com contas em aberto");
        }

        if (table.getStatus() != TableStatus.AVAILABLE) {
            throw new IllegalStateException("Mesa já ocupada ou indisponível");
        }

        customers.stream()
                .filter(customer -> !customer.isActive())
                .findAny()
                .ifPresent(customer -> {
                    throw new IllegalStateException("Não é possível atrelar um cliente inativo a uma mesa");
                });

        table.setCustomers(customers);
        table.setStatus(TableStatus.OCCUPIED);
    }

    public void freeTable(Table table) {
        if (billService.existsByTableAndIsPaid(table.getId(), false)) {
            throw new IllegalStateException("Não é possível liberar uma mesa com contas em aberto");
        }

        if (table.getStatus() == TableStatus.AVAILABLE) {
            throw new IllegalStateException("Mesa já está livre");
        }

        table.setCustomers(null);
        table.setStatus(TableStatus.AVAILABLE);
    }
}
