package menu;

import product.Product;

import java.util.List;
import java.util.NoSuchElementException;

public class MenuService {
    private final MenuDAO menuDAO;

    public MenuService(MenuDAO menuDAO) {
        this.menuDAO = menuDAO;
    }

    public Menu findById(int id) {
        return menuDAO.findById(id).orElseThrow(() -> new NoSuchElementException("Menu não encontrado"));
    }

    public List<Menu> findAll() {
        return menuDAO.findAll();
    }

    public Menu save(Menu entity) {
        return menuDAO.save(entity);
    }

    public void update(Menu entity) {
        menuDAO.update(entity);
    }

    public void delete(Menu entity) {
        menuDAO.delete(entity);
    }

    public void addProduct(Menu menu, Product product) {
        menu.getProducts().add(product);
    }

    public void removeProduct(Menu menu, Product product) {
        menu.getProducts().remove(product);
    }
}
