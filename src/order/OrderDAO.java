package order;

import interfaces.Crud;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class OrderDAO implements Crud<Order> {
    private final List<Order> orderList;

    public OrderDAO() {
        this.orderList = new ArrayList<>();
    }

    @Override
    public Optional<Order> findById(int id) {
        return orderList.stream()
                .filter(order -> order.getId() == id)
                .findFirst();
    }

    @Override
    public List<Order> findAll() {
        return this.orderList;
    }

    @Override
    public void save(Order entity) {
        this.orderList.add(entity);
    }

    @Override
    public void update(Order entity) {
        var order = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Pedido não encontrado"));
        this.orderList.set(orderList.indexOf(order), entity);
    }

    @Override
    public void delete(Order entity) {
        var order = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Pedido não encontrado"));
        this.orderList.remove(order);
    }
}
