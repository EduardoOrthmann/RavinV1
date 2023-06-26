package payment;

import interfaces.Crud;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class PaymentDAO implements Crud<Payment> {
    private final List<Payment> paymentList;

    public PaymentDAO() {
        this.paymentList = new ArrayList<>();
    }

    @Override
    public Optional<Payment> findById(int id) {
        return paymentList.stream()
                .filter(payment -> payment.getId() == id)
                .findFirst();
    }

    @Override
    public List<Payment> findAll() {
        return this.paymentList;
    }

    @Override
    public void save(Payment entity) {
        this.paymentList.add(entity);
    }

    @Override
    public void update(Payment entity) {
        var payment = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Pagamento não encontrado"));
        this.paymentList.set(paymentList.indexOf(payment), entity);
    }

    @Override
    public void delete(Payment entity) {
        var payment = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Pagamento não encontrado"));
        this.paymentList.remove(payment);
    }
}
