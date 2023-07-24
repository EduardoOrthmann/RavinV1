package user;

import enums.Role;

public class User {
    private static int lastId = 0;
    private int id;
    private String username;
    private String password;
    private Role role;
    private String token;

    // insert
    public User(String username, String password, Role role) {
        this.id = ++lastId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // update
    public User(int id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
