package com.example.dstock;

public class User {
    private String name;
    private String passwordSaltedHash;
    private String email;

    public String getName() {
        return name;
    }

    public String getPasswordSaltedHash() {
        return passwordSaltedHash;
    }

    public String getEmail() {
        return email;
    }

    User(String name, String passwordSaltedHash, String email){
        this.name=name;
        this.passwordSaltedHash=passwordSaltedHash;
        this.email=email;
    }

}
