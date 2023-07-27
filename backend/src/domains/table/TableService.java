package domains.table;

import domains.bill.BillService;
import domains.customer.Customer;
import enums.TableStatus;
import utils.Constants;

import java.util.HashSet;
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
        return tableDAO.findById(id).orElseThrow(() -> new NoSuchElementException(Constants.TABLE_NOT_FOUND));
    }

    public boolean existsById(int tableId) {
        return tableDAO.findById(tableId).isPresent();
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

        for (var customer : customers) {
            if (!customer.isActive()) {
                throw new IllegalStateException("Não é possível atrelar um cliente inativo a uma mesa");
            }
        }

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

        table.setCustomers(new HashSet<>());
        table.setStatus(TableStatus.AVAILABLE);
    }
}
