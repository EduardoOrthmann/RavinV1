package domains.order;

import domains.customer.CustomerService;
import domains.orderItem.OrderItem;
import domains.orderItem.OrderItemRepository;
import domains.payment.PaymentService;
import enums.OrderItemStatus;
import interfaces.Payment;
import utils.Constants;
import utils.DateUtils;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerService customerService;
    private final PaymentService paymentService;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, CustomerService customerService, PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerService = customerService;
        this.paymentService = paymentService;
    }

    public Order findById(int id) {
        return orderRepository.findById(id).orElseThrow(() -> new NoSuchElementException(Constants.ORDER_NOT_FOUND));
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order save(Order entity) {
        Order order = orderRepository.save(entity);
        Set<OrderItem> orderItems = new HashSet<>();

        entity.getOrderItems().forEach(orderItem -> {
            orderItem.setOrderId(order.getId());
            orderItems.add(orderItemRepository.save(orderItem));
        });

        order.setOrderItems(orderItems);
        updateTotalPrice(order);

        orderRepository.update(order);

        return order;
    }

    public void update(Order entity) {
        orderRepository.update(entity);
    }

    public void delete(Order entity) {
        orderRepository.delete(entity);
    }

    public void addOrderItem(Order order, OrderItem orderItem) {
        orderItem.setOrderId(order.getId());
        OrderItem item = orderItemRepository.save(orderItem);

        order.getOrderItems().add(item);
        updateTotalPrice(order);

        orderRepository.update(order);
    }

    public void updateTotalPrice(Order order) {
        order.setTotalPrice(
                order.getOrderItems().stream()
                        .filter(orderItem -> orderItem.getStatus() != OrderItemStatus.CANCELED)
                        .mapToDouble(OrderItem::getTotalPrice)
                        .sum()
        );
    }

    public boolean existsByTableAndIsPaid(int tableId, boolean isPaid) {
        return !orderRepository.findAllByTableAndIsPaid(tableId, isPaid).isEmpty();
    }

    public List<Order> findByTableAndIsPaid(int tableId, boolean isPaid) {
        return orderRepository.findAllByTableAndIsPaid(tableId, isPaid);
    }

    public void closeOrder(Order order, double amount, Payment method) {
        var amountToPay = order.getTotalPrice();
        var customer = customerService.findById(order.getCustomerId());

        if (DateUtils.isBirthday(customer.getBirthDate())) {
            amountToPay = paymentService.applyDiscount(amountToPay, 10);
        }

        paymentService.processPayment(method, amount, amountToPay);
        order.setPaid(true);
    }
}
