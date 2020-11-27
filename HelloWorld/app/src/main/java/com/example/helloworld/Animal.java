package com.example.helloworld;

public class Animal {
    private String name;
    public Animal() {
        System.out.println("Animal !");
    }

    public Animal(String name) {
        this.name = name;
        System.out.println("Animal With Name");
    }
}
