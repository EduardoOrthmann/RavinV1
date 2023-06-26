package payment;

import java.util.List;
import java.util.NoSuchElementException;

public class PaymentService {
    private final PaymentDAO paymentDAO;

    public PaymentService() {
        this.paymentDAO = new PaymentDAO();
    }

    public Payment findById(int id) {
        return paymentDAO.findById(id).orElseThrow(() -> new NoSuchElementException("Menu não encontrado"));
    }

    public List<Payment> findAll() {
        return paymentDAO.findAll();
    }

    public void save(Payment entity) {
        paymentDAO.save(entity);
    }

    public void update(Payment entity) {
        paymentDAO.update(entity);
    }

    public void delete(Payment entity) {
        paymentDAO.delete(entity);
    }

    // TODO
    public double pay(double cash) {
        return 0;
    }

    // TODO
    public double applyDiscount(int discount) {
        return 0;
    }

    // TODO
    public void printReceipt() {

    }

    // TODO
    public double calculateTotalPrice() {
        return 0;
    }
}
