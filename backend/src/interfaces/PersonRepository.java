package interfaces;

import java.util.Optional;

public interface PersonRepository<T> {
    Optional<T> findByUserId(int userId);
    Optional<T> findByCpf(String cpf);
}
