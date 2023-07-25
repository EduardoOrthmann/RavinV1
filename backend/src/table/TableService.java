package table;

import customer.Customer;
import enums.TableStatus;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class TableService {
    private final TableDAO tableDAO;

    public TableService(TableDAO tableDAO) {
        this.tableDAO = tableDAO;
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
        if (table.getStatus() != TableStatus.AVAILABLE) {
            throw new IllegalStateException("Mesa já ocupada ou indisponível");
        }

        table.setCustomers(customers);
        table.setStatus(TableStatus.OCCUPIED);
    }

    public void freeTable(Table table) {
        if (table.getStatus() == TableStatus.AVAILABLE) {
            throw new IllegalStateException("Mesa já está livre");
        }

        table.setCustomers(null);
        table.setStatus(TableStatus.AVAILABLE);
    }
}
