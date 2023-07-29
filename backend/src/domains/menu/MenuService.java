package domains.menu;

import domains.product.Product;
import domains.product.ProductService;
import utils.Constants;

import java.util.List;
import java.util.NoSuchElementException;

public class MenuService {
    private final MenuRepository menuRepository;
    private final ProductService productService;

    public MenuService(MenuRepository menuRepository, ProductService productService) {
        this.menuRepository = menuRepository;
        this.productService = productService;
    }

    public Menu findById(int id) {
        return menuRepository.findById(id).orElseThrow(() -> new NoSuchElementException(Constants.MENU_NOT_FOUND));
    }

    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    public Menu save(Menu entity) {
        var menu = menuRepository.save(entity);

        entity.getProducts().forEach(product -> {
            product.setMenuId(menu.getId());
            productService.update(product);
        });

        return menu;
    }

    public void update(Menu entity) {
        menuRepository.update(entity);
    }

    public void delete(int entityId) {
        menuRepository.delete(findById(entityId));
    }

    public void addProduct(Menu menu, Product product) {
        product.setMenuId(menu.getId());
        productService.update(product);
    }

    public void removeProduct(Product product) {
        product.setMenuId(null);
        productService.update(product);
    }
}
