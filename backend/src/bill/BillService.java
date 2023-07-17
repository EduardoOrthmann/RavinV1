package bill;

import order.Order;

import java.util.List;
import java.util.NoSuchElementException;

public class BillService {
    private final BillDAO billDAO;

    public BillService(BillDAO billDAO) {
        this.billDAO = billDAO;
    }

    public Bill findById(int id) {
        return billDAO.findById(id).orElseThrow(() -> new NoSuchElementException("Comanda não encontrada"));
    }

    public List<Bill> findAll() {
        return billDAO.findAll();
    }

    public void save(Bill entity) {
        billDAO.save(entity);
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

    public void removeOrder(Bill bill, Order order) {
        bill.getOrders().remove(order);
        updateTotalPrice(bill);
    }

    public void updateTotalPrice(Bill bill) {
        bill.setTotalPrice(bill.getOrders().stream().mapToDouble(Order::getPrice).sum());
    }

    // TODO
    public void closeBill(Bill bill) {
    }
}
