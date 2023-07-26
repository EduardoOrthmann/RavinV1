package domains.order;

import interfaces.Crud;
import utils.Constants;

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
    public Order save(Order entity) {
        this.orderList.add(entity);
        return entity;
    }

    @Override
    public void update(Order entity) {
        var order = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException(Constants.ORDER_NOT_FOUND));
        this.orderList.set(orderList.indexOf(order), entity);
    }

    @Override
    public void delete(Order entity) {
        var order = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException(Constants.ORDER_NOT_FOUND));
        this.orderList.remove(order);
    }
}
