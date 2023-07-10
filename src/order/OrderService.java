package order;

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

    public void save(Order entity) {
        orderDAO.save(entity);
    }

    public void update(Order entity) {
        orderDAO.update(entity);
    }

    public void delete(Order entity) {
        orderDAO.delete(entity);
    }

    public void addNote(Order order, String note) {
        order.getNotes().add(note);
    }

    public void updateNote(Order order, String note, String updatedNote) {
        order.getNotes().set(order.getNotes().indexOf(note), updatedNote);
    }

    public void deleteNote(Order order, String note) {
        order.getNotes().remove(note);
    }
}
