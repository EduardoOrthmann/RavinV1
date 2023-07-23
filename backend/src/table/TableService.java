package table;

import customer.Customer;
import enums.OrderStatus;
import order.Order;

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

    public Table save(Table entity) {
        return tableDAO.save(entity);
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

    public void removeCustomer(Table table, Customer customer) {
        table.getCustomers().remove(customer);
    }

    public void addOrder(Table table, Order order) {
        table.getOrders().add(order);
    }

    public void removeOrder(Table table, Order order) {
        table.getOrders().remove(order);
    }

    public boolean hasOpenOrders(Table table) {
        return table.getOrders().stream()
                .anyMatch(order -> order.getStatus() != OrderStatus.CANCELED && order.getStatus() != OrderStatus.DELIVERED);
    }

    public boolean isAtMaxCapacity(Table table) {
        return table.getCustomers().size() == table.getMaxCapacity();
    }
}
