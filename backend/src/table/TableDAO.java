package table;

import interfaces.Crud;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class TableDAO implements Crud<Table> {
    private final List<Table> tableList;

    public TableDAO() {
        this.tableList = new ArrayList<>();
    }

    @Override
    public Optional<Table> findById(int id) {
        return tableList.stream()
                .filter(table -> table.getId() == id)
                .findFirst();
    }

    @Override
    public List<Table> findAll() {
        return this.tableList;
    }

    @Override
    public void save(Table entity) {
        this.tableList.add(entity);
    }

    @Override
    public void update(Table entity) {
        var table = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Mesa não encontrada"));
        this.tableList.set(tableList.indexOf(table), entity);
    }

    @Override
    public void delete(Table entity) {
        var table = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Mesa não encontrada"));
        this.tableList.remove(table);
    }
}
