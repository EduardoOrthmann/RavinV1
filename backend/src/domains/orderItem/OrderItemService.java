package domains.orderItem;

import domains.order.Order;
import domains.order.OrderService;
import domains.employee.Employee;
import domains.orderItemComment.OrderItemComment;
import domains.orderItemComment.OrderItemCommentRepository;
import enums.OrderItemStatus;
import exceptions.UnauthorizedRequestException;
import utils.Constants;

import java.util.List;
import java.util.NoSuchElementException;

public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;
    private final OrderItemCommentRepository orderItemCommentRepository;

    public OrderItemService(OrderItemRepository orderItemRepository, OrderService orderService, OrderItemCommentRepository orderItemCommentRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderService = orderService;
        this.orderItemCommentRepository = orderItemCommentRepository;
    }

    public OrderItem findById(int id) {
        return orderItemRepository.findById(id).orElseThrow(() -> new NoSuchElementException(Constants.ORDER_ITEM_NOT_FOUND));
    }

    public List<OrderItem> findAll() {
        return orderItemRepository.findAll();
    }

    public OrderItem save(OrderItem entity) {
        if (entity.getNotes() != null) {
            entity.getNotes().forEach(note -> orderItemCommentRepository.save(new OrderItemComment(entity.getId(), note)));
        }

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

    public void delete(int entityId) {
        orderItemRepository.delete(findById(entityId));
    }

    public void updateStatus(OrderItem orderItem, OrderItemStatus status, Integer updatedBy) throws UnauthorizedRequestException {
        if (orderItem.getEmployeeId() == null) {
            throw new UnauthorizedRequestException(Constants.ORDER_ITEM_NOT_TAKEN);
        }

        if (orderItem.getStatus() == OrderItemStatus.DELIVERED || orderItem.getStatus() == OrderItemStatus.CANCELED) {
            throw new IllegalArgumentException(Constants.ORDER_ITEM_ALREADY_DELIVERED_OR_CANCELED);
        }

        orderItem.setUpdatedBy(updatedBy);
        orderItem.setStatus(status);
        update(orderItem);

        if (status == OrderItemStatus.CANCELED) {
            Order order = orderService.findById(orderItem.getOrderId());
            orderService.updateTotalPrice(order);
            orderService.update(order);
        }
    }

    public void cancelOrder(OrderItem orderItem, Integer updatedBy) {
        if (orderItem.getStatus() != OrderItemStatus.WAITING) {
            throw new IllegalArgumentException(Constants.ORDER_ITEM_ALREADY_TAKEN);
        }

        orderItem.setUpdatedBy(updatedBy);
        orderItem.setStatus(OrderItemStatus.CANCELED);
        update(orderItem);

        Order order = orderService.findById(orderItem.getOrderId());
        orderService.updateTotalPrice(order);
        orderService.update(order);
    }

    private void updateTotalPrice(OrderItem orderItem) {
        orderItem.setTotalPrice(orderItem.getProduct().getSalePrice() * orderItem.getQuantity());
    }

    public void takeOrderItem(OrderItem orderItem, Employee employee) {
        if (orderItem.getStatus() == OrderItemStatus.CANCELED) {
            throw new IllegalArgumentException(Constants.ORDER_ITEM_ALREADY_CANCELED);
        }

        orderItem.setEmployeeId(employee.getId());
        orderItem.setStatus(OrderItemStatus.PREPARING);
        orderItem.setUpdatedBy(employee.getUser().getId());

        update(orderItem);
    }

    public List<OrderItem> findAllByOrderId(int orderId) {
        return orderItemRepository.findAllByOrderId(orderId);
    }
}
