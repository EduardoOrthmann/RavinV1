package interfaces;

import java.util.List;
import java.util.Optional;

public interface Crud<T> {
    Optional<T> findById(int id);
    List<T> findAll();
    T save(T entity);
    void update(T entity);
    void delete(T entity);
}
