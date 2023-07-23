package order;

import bill.BillService;
import employee.Employee;
import enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

public class OrderService {
    private final OrderDAO orderDAO;
    private final BillService billService;

    public OrderService(OrderDAO orderDAO, BillService billService) {
        this.orderDAO = orderDAO;
        this.billService = billService;
    }

    public Order findById(int id) {
        return orderDAO.findById(id).orElseThrow(() -> new NoSuchElementException("Pedido não encontrado"));
    }

    public List<Order> findAll() {
        return orderDAO.findAll();
    }

    public Order save(Order entity) {
        updateTotalPrice(entity);
        return orderDAO.save(entity);
    }

    public void update(Order entity) {
        if (entity.getStatus() != OrderStatus.WAITING) {
            throw new IllegalArgumentException("Não é possível alterar um pedido que não esteja aguardando");
        }

        updateTotalPrice(entity);
        orderDAO.update(entity);
    }

    public void delete(Order entity) {
        orderDAO.delete(entity);
    }

    public void updateStatus(Order order, OrderStatus status, Integer updatedBy) {
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalArgumentException("Não é possível alterar o pedido");
        }

        order.setUpdatedAt(LocalDateTime.now());
        order.setUpdatedBy(updatedBy);
        order.setStatus(status);

        if (status == OrderStatus.CANCELED) {
            billService.updateTotalPrice(billService.findByOrderId(order.getId()));
        }
    }

    public void cancelOrder(Order order, Integer updatedBy) {
        if (order.getStatus() != OrderStatus.WAITING) {
            throw new IllegalArgumentException("Não é possível cancelar o pedido pois o mesmo já foi aceito");
        }

        order.setUpdatedAt(LocalDateTime.now());
        order.setUpdatedBy(updatedBy);
        order.setStatus(OrderStatus.CANCELED);

        billService.updateTotalPrice(billService.findByOrderId(order.getId()));
    }

    private void updateTotalPrice(Order order) {
        order.setPrice(order.getProduct().getSalePrice() * order.getQuantity());
    }

    public void takeOrder(Order order, Employee employee) {
        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalArgumentException("Não é possível aceitar o pedido pois o mesmo foi cancelado");
        }

        order.setEmployeeId(employee.getId());
        order.setStatus(OrderStatus.PREPARING);
        order.setUpdatedAt(LocalDateTime.now());
        order.setUpdatedBy(employee.getUser().getId());
    }
}
