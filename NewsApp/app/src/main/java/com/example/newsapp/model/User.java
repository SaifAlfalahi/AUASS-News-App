package com.example.newsapp.model;

public class User {
    enum Type{
        ADMIN, REGULAR
    }

    private Type type;
    private String fname;
    private String lname;
    private String email;
    private String password;

    public User(String email, String password){
        this.email = email;
        this.password = password;
    }
    public User(){
        type = Type.REGULAR;
    }


    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }
    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setAdmin(){
        type = Type.ADMIN;
    }
    public boolean isAdmin(){
        return type == Type.ADMIN;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
