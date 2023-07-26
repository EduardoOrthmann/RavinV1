package domains.bill;

import domains.customer.CustomerService;
import enums.OrderStatus;
import interfaces.Payment;
import domains.order.Order;
import domains.payment.PaymentService;
import utils.Constants;
import utils.DateUtils;

import java.util.List;
import java.util.NoSuchElementException;

public class BillService {
    private final BillDAO billDAO;
    private final CustomerService customerService;
    private final PaymentService paymentService;

    public BillService(BillDAO billDAO, CustomerService customerService, PaymentService paymentService) {
        this.billDAO = billDAO;
        this.customerService = customerService;
        this.paymentService = paymentService;
    }

    public Bill findById(int id) {
        return billDAO.findById(id).orElseThrow(() -> new NoSuchElementException(Constants.BILL_NOT_FOUND));
    }

    public List<Bill> findAll() {
        return billDAO.findAll();
    }

    public Bill save(Bill entity) {
        updateTotalPrice(entity);
        return billDAO.save(entity);
    }

    public void update(Bill entity) {
        billDAO.update(entity);
    }

    public void delete(Bill entity) {
        billDAO.delete(entity);
    }

    public void addOrder(Bill bill, Order order) {
        bill.getOrders().add(order);
        updateTotalPrice(bill);
    }

    public void updateTotalPrice(Bill bill) {
        bill.setTotalPrice(
                bill.getOrders().stream()
                        .filter(order -> order.getStatus() != OrderStatus.CANCELED)
                        .mapToDouble(Order::getPrice)
                        .sum()
        );
    }

    public Bill findByOrderId(int orderId) {
        return billDAO.findByOrderId(orderId).orElseThrow(() -> new NoSuchElementException(Constants.BILL_NOT_FOUND));
    }

    public boolean existsByTableAndIsPaid(int tableId, boolean isPaid) {
        return billDAO.existsByTableAndIsPaid(tableId, isPaid);
    }

    public List<Bill> findByTableAndIsPaid(int tableId, boolean isPaid) {
        return billDAO.findByTableAndIsPaid(tableId, isPaid);
    }

    public void closeBill(Bill bill, double amount, Payment method) {
        var amountToPay = bill.getTotalPrice();
        var customer = customerService.findById(bill.getCustomerId());

        if (DateUtils.isBirthday(customer.getBirthDate())) {
            amountToPay = paymentService.applyDiscount(amountToPay, 10);
        }

        paymentService.processPayment(method, amount, amountToPay);
        bill.setPaid(true);
    }
}
