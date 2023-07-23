package order;

import enums.OrderStatus;

import java.util.List;
import java.util.NoSuchElementException;

public class OrderService {
    private final OrderDAO orderDAO;

    public OrderService(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
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
        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Não é possível alterar o pedido");
        }

        order.setUpdatedBy(updatedBy);
        order.setStatus(status);
    }

    private void updateTotalPrice(Order order) {
        order.setPrice(order.getProduct().getSalePrice() * order.getQuantity());
    }
}
