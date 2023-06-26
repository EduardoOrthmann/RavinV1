package command;

import enums.OrderStatus;
import interfaces.Crud;
import table.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CommandDAO  implements Crud<Command> {
    private final List<Command> commandList;

    public CommandDAO() {
        this.commandList = new ArrayList<>();
    }

    @Override
    public Optional<Command> findById(int id) {
        return commandList.stream()
                .filter(command -> command.getId() == id)
                .findFirst();
    }

    @Override
    public List<Command> findAll() {
        return this.commandList;
    }

    @Override
    public void save(Command entity) {
        this.commandList.add(entity);
    }

    @Override
    public void update(Command entity) {
        var command = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Comanda não encontrada"));
        this.commandList.set(commandList.indexOf(command), entity);
    }

    @Override
    public void delete(Command entity) {
        var command = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Comanda não encontrada"));
        this.commandList.remove(command);
    }

    // TODO
    public List<Command> findByStatus(OrderStatus status) {
        return null;
    }

    // TODO
    public List<Command> findByStatusAndTable(OrderStatus status, Table table) {
        return null;
    }
}
