package com.chaitupenjudcoder.datapojos;

public class User {
    private String email;
    private String name;
    private String created;
    private String last_login;

    public User() {
    }

    public User(String email, String name, String created, String last_login) {
        this.email = email;
        this.name = name;
        this.created = created;
        this.last_login = last_login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }
}
