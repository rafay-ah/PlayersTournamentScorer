package com.players.nest;

public class DataModel {

    private String name;
    private String email;

    public DataModel(String name, String age) {
        this.name = name;
        this.email = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String age) {
        this.email = age;
    }

}
