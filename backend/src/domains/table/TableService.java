package domains.table;

import domains.customer.CustomerService;
import domains.order.OrderService;
import domains.customer.Customer;
import enums.TableStatus;
import utils.Constants;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class TableService {
    private final TableRepository tableRepository;
    private final OrderService orderService;
    private final CustomerService customerService;

    public TableService(TableRepository tableRepository, OrderService orderService, CustomerService customerService) {
        this.tableRepository = tableRepository;
        this.orderService = orderService;
        this.customerService = customerService;
    }

    public Table findById(int id) {
        return tableRepository.findById(id).orElseThrow(() -> new NoSuchElementException(Constants.TABLE_NOT_FOUND));
    }

    public List<Table> findAll() {
        return tableRepository.findAll();
    }

    public Table save(Table entity) {
        return tableRepository.save(entity);
    }

    public void update(Table entity) {
        tableRepository.update(entity);
    }

    public void delete(int entityId) {
        tableRepository.delete(findById(entityId));
    }

    public boolean existsById(int tableId) {
        return tableRepository.findById(tableId).isPresent();
    }

    public void occupyTable(Table table, Set<Customer> customers) {
        if (orderService.existsByTableAndIsPaid(table.getId(), false)) {
            throw new IllegalStateException(Constants.TABLE_OCCUPIED);
        }

        if (table.getStatus() != TableStatus.AVAILABLE) {
            throw new IllegalStateException(Constants.TABLE_OCCUPIED);
        }

        for (var customer : customers) {
            if (!customer.isActive()) {
                throw new IllegalStateException(Constants.INACTIVE_CUSTOMER);
            }
        }

        customers.forEach(customer -> {
            customer.setTableId(table.getId());
            customerService.update(customer);
        });

        table.setStatus(TableStatus.OCCUPIED);
        update(table);
    }

    public void freeTable(Table table) {
        if (orderService.existsByTableAndIsPaid(table.getId(), false)) {
            throw new IllegalStateException(Constants.TABLE_NOT_PAID_YET);
        }

        if (table.getStatus() == TableStatus.AVAILABLE) {
            throw new IllegalStateException(Constants.TABLE_ALREADY_FREE);
        }

        var customers = customerService.findAllByTableId(table.getId());

        customers.forEach(customer -> {
            customer.setTableId(null);
            customerService.update(customer);
        });

        table.setStatus(TableStatus.AVAILABLE);
        update(table);
    }
}
