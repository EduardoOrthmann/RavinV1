package domains.orderItem;

import domains.order.OrderService;
import domains.employee.Employee;
import enums.OrderItemStatus;
import exceptions.UnauthorizedRequestException;
import utils.Constants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;

    public OrderItemService(OrderItemRepository orderItemRepository, OrderService orderService) {
        this.orderItemRepository = orderItemRepository;
        this.orderService = orderService;
    }

    public OrderItem findById(int id) {
        return orderItemRepository.findById(id).orElseThrow(() -> new NoSuchElementException(Constants.ORDER_ITEM_NOT_FOUND));
    }

    public List<OrderItem> findAll() {
        return orderItemRepository.findAll();
    }

    public OrderItem save(OrderItem entity) {
        updateTotalPrice(entity);
        return orderItemRepository.save(entity);
    }

    public void update(OrderItem entity) {
        if (entity.getStatus() != OrderItemStatus.WAITING) {
            throw new IllegalArgumentException(Constants.ORDER_ITEM_STATUS_NOT_ALLOWED);
        }

        updateTotalPrice(entity);
        orderItemRepository.update(entity);
    }

    public void delete(OrderItem entity) {
        orderItemRepository.delete(entity);
    }

    public void updateStatus(OrderItem orderItem, OrderItemStatus status, Integer updatedBy) throws UnauthorizedRequestException {
        if (orderItem.getEmployeeId() == null) {
            throw new UnauthorizedRequestException(Constants.ORDER_ITEM_NOT_TAKEN);
        }

        if (orderItem.getStatus() == OrderItemStatus.DELIVERED || orderItem.getStatus() == OrderItemStatus.CANCELED) {
            throw new IllegalArgumentException(Constants.ORDER_ITEM_ALREADY_DELIVERED_OR_CANCELED);
        }

        orderItem.setUpdatedAt(LocalDateTime.now());
        orderItem.setUpdatedBy(updatedBy);
        orderItem.setStatus(status);

        if (status == OrderItemStatus.CANCELED) {
            orderService.updateTotalPrice(orderService.findByOrderItemId(orderItem.getId()));
        }
    }

    public void cancelOrder(OrderItem orderItem, Integer updatedBy) {
        if (orderItem.getStatus() != OrderItemStatus.WAITING) {
            throw new IllegalArgumentException(Constants.ORDER_ITEM_ALREADY_TAKEN);
        }

        orderItem.setUpdatedAt(LocalDateTime.now());
        orderItem.setUpdatedBy(updatedBy);
        orderItem.setStatus(OrderItemStatus.CANCELED);

        orderService.updateTotalPrice(orderService.findByOrderItemId(orderItem.getId()));
    }

    private void updateTotalPrice(OrderItem orderItem) {
        orderItem.setPrice(orderItem.getProduct().getSalePrice() * orderItem.getQuantity());
    }

    public void takeOrderItem(OrderItem orderItem, Employee employee) {
        if (orderItem.getStatus() == OrderItemStatus.CANCELED) {
            throw new IllegalArgumentException(Constants.ORDER_ITEM_ALREADY_CANCELED);
        }

        orderItem.setEmployeeId(employee.getId());
        orderItem.setStatus(OrderItemStatus.PREPARING);
        orderItem.setUpdatedAt(LocalDateTime.now());
        orderItem.setUpdatedBy(employee.getUser().getId());
    }
}
