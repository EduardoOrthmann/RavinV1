package domains.order;

import domains.customer.CustomerService;
import domains.orderItem.OrderItem;
import domains.payment.PaymentService;
import enums.OrderItemStatus;
import interfaces.Payment;
import utils.Constants;
import utils.DateUtils;

import java.util.List;
import java.util.NoSuchElementException;

public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final PaymentService paymentService;

    public OrderService(OrderRepository orderRepository, CustomerService customerService, PaymentService paymentService) {
        this.orderRepository = orderRepository;
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
        updateTotalPrice(entity);
        return orderRepository.save(entity);
    }

    public void update(Order entity) {
        orderRepository.update(entity);
    }

    public void delete(Order entity) {
        orderRepository.delete(entity);
    }

    public void addOrderItem(Order order, OrderItem orderItem) {
        order.getOrderItems().add(orderItem);
        updateTotalPrice(order);
    }

    public void updateTotalPrice(Order order) {
        order.setTotalPrice(
                order.getOrderItems().stream()
                        .filter(orderItem -> orderItem.getStatus() != OrderItemStatus.CANCELED)
                        .mapToDouble(OrderItem::getPrice)
                        .sum()
        );
    }

    public Order findByOrderItemId(int orderItemId) {
        return orderRepository.findByOrderItemId(orderItemId).orElseThrow(() -> new NoSuchElementException(Constants.ORDER_NOT_FOUND));
    }

    public boolean existsByTableAndIsPaid(int tableId, boolean isPaid) {
        return !orderRepository.findByTableAndIsPaid(tableId, isPaid).isEmpty();
    }

    public List<Order> findByTableAndIsPaid(int tableId, boolean isPaid) {
        return orderRepository.findByTableAndIsPaid(tableId, isPaid);
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
