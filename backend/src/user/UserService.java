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

    public String login(String username, String password) {
        User user = findByUsername(username);

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Senha incorreta");
        }

        var token = generateToken();
        user.setToken(token);
        return token;
    }

    public User findByToken(String token) {
        return userRepository.findByToken(token).orElseThrow(() -> new NoSuchElementException("Token não encontrado"));
    }

    public void logout(String token) {
        User user = findByToken(token);
        user.setToken(null);
    }
}
