package com.example.newsapp;

public class UserH
{
    private String email;
    private String password;
    private String name;
    private int icon;
    private boolean admin;

    public UserH(String email, String password, String name, int icon)
    {
        this.email = email;
        this.password = password;
        this.name = name;
        this.icon = icon;
        admin = false;
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public String getUserName() {return name;}

    public void setUserName(String name) {this.name = name;}

    public int getIcon() {return icon;}

    public void setIcon(int icon) {this.icon = icon;}

    public boolean isAdmin() {return admin;}

    public void setAdmin(boolean admin) {this.admin = admin;}
}
