package com.jaisel.tictactoe.Utils;

/**
 * Created by jaisel on 11/6/17.
 */

public class User {
    public static final int STATUS_NONE = 0;
    public static final int STATUS_ACCEPTED = 1 ;
    public static final int STATUS_SENT = 2;
    public static final int STATUS_RECEIVED = 3;

    private String name;
    private String id;
    private String email;
    private int status;

    public User(String uid, String name, String email) {
        this.id = uid;
        this.name = name;
        this.email = email;
    }

    public User(String name) {
        this(null, name, null);
    }
    public User(String name, String email) {
        this(null, name, email);
    }

    public User() {
        this(null, null, null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
