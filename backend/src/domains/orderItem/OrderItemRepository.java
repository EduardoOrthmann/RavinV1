package domains.orderItem;

import interfaces.Crud;
import utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class OrderItemRepository implements Crud<OrderItem> {
    private final List<OrderItem> orderItemList;

    public OrderItemRepository() {
        this.orderItemList = new ArrayList<>();
    }

    @Override
    public Optional<OrderItem> findById(int id) {
        return orderItemList.stream()
                .filter(orderItem -> orderItem.getId() == id)
                .findFirst();
    }

    @Override
    public List<OrderItem> findAll() {
        return this.orderItemList;
    }

    @Override
    public OrderItem save(OrderItem entity) {
        this.orderItemList.add(entity);
        return entity;
    }

    @Override
    public void update(OrderItem entity) {
        var orderItem = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException(Constants.ORDER_ITEM_NOT_FOUND));
        this.orderItemList.set(orderItemList.indexOf(orderItem), entity);
    }

    @Override
    public void delete(OrderItem entity) {
        var orderItem = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException(Constants.ORDER_ITEM_NOT_FOUND));
        this.orderItemList.remove(orderItem);
    }
}
