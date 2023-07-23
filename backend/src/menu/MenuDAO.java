package menu;

import interfaces.Crud;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class MenuDAO implements Crud<Menu> {
    private final List<Menu> menuList;

    public MenuDAO() {
        this.menuList = new ArrayList<>();
    }

    @Override
    public Optional<Menu> findById(int id) {
        return menuList.stream()
                .filter(menu -> menu.getId() == id)
                .findFirst();
    }

    @Override
    public List<Menu> findAll() {
        return this.menuList;
    }

    @Override
    public Menu save(Menu entity) {
        this.menuList.add(entity);
        return entity;
    }

    @Override
    public void update(Menu entity) {
        var menu = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Menu não encontrado"));
        this.menuList.set(menuList.indexOf(menu), entity);
    }

    @Override
    public void delete(Menu entity) {
        var menu = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Menu não encontrado"));
        this.menuList.remove(menu);
    }
}
