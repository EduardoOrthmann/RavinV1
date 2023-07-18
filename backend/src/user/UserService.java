package user;

import java.util.NoSuchElementException;
import java.util.Random;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));
    }

    public void save(User entity) {
        if (userRepository.findByUsername(entity.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Usuário já cadastrado");
        }

        entity.setToken(generateToken());
        userRepository.save(entity);
    }

    public void update(User entity) {
        userRepository.update(entity);
    }

    public void delete(User entity) {
        userRepository.delete(entity);
    }

    public String generateToken() {
        Random random = new Random();
        StringBuilder token = new StringBuilder();

        random.ints(10, 0, 10).forEach(token::append);

        return token.toString();
    }
}
