package table;

import customer.Customer;

import java.util.List;
import java.util.NoSuchElementException;

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

    public void save(Table entity) {
        tableDAO.save(entity);
    }

    public void update(Table entity) {
        tableDAO.update(entity);
    }

    public void delete(Table entity) {
        tableDAO.delete(entity);
    }

    public void addCustomer(Table table, Customer customer) {
        table.getCustomers().add(customer);
    }

    public void deleteCustomer(Table table, Customer customer) {
        table.getCustomers().remove(customer);
    }

    // TODO
    public boolean hasOpenOrders(Table table) {
        return false;
    }

    public boolean isAtMaxCapacity(Table table) {
        return table.getCustomers().size() == table.getMaxCapacity();
    }
}
