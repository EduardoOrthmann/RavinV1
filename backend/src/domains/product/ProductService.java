package domains.product;

import utils.Constants;

import java.util.List;
import java.util.NoSuchElementException;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product findById(int id) {
        return productRepository.findById(id).orElseThrow(() -> new NoSuchElementException(Constants.PRODUCT_NOT_FOUND));
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product save(Product entity) {
        return productRepository.save(entity);
    }

    public void update(Product entity) {
        productRepository.update(entity);
    }

    public void delete(int entityId) {
        productRepository.delete(findById(entityId));
    }

    public List<Product> findByMenuId(int menuId) {
        return productRepository.findByMenuId(menuId);
    }
}
