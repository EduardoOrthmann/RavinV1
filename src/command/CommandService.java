package command;

import order.Order;
import product.Product;

import java.util.List;
import java.util.NoSuchElementException;

public class CommandService {
    private final CommandDAO commandDAO;

    public CommandService() {
        this.commandDAO = new CommandDAO();
    }

    public Command findById(int id) {
        return commandDAO.findById(id).orElseThrow(() -> new NoSuchElementException("Comanda não encontrada"));
    }

    public List<Command> findAll() {
        return commandDAO.findAll();
    }

    public void save(Command entity) {
        commandDAO.save(entity);
    }

    public void update(Command entity) {
        commandDAO.update(entity);
    }

    public void delete(Command entity) {
        commandDAO.delete(entity);
    }

    public void addOrder(Command command, Order order) {
        command.getOrders().add(order);
    }

    public void deleteOrder(Command command, Order order) {
        command.getOrders().remove(order);
    }

    // TODO
    public void closeCommand(Command command, double cash) {

    }
}
