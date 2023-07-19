package user;

import interfaces.Crud;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class UserRepository implements Crud<User> {
    private final List<User> userList;

    public UserRepository() {
        this.userList = new ArrayList<>();
    }

    @Override
    public Optional<User> findById(int id) {
        return this.userList.stream()
                .filter(user -> user.getId() == id)
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return this.userList;
    }

    @Override
    public void save(User entity) {
        this.userList.add(entity);
    }

    @Override
    public void update(User entity) {
        var user = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));
        this.userList.set(userList.indexOf(user), entity);
    }

    @Override
    public void delete(User entity) {
        var user = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));
        this.userList.remove(user);
    }

    public Optional<User> findByUsername(String username) {
        return this.userList.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    public Optional<User> findByToken(String token) {
        return this.userList.stream()
                .filter(user -> user.getToken() != null)
                .filter(user -> user.getToken().equals(token))
                .findFirst();
    }
}
