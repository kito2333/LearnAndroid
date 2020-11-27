package com.example.helloworld;

public class Dog extends Animal {
    public Dog() {
        System.out.println("Dog !");
    }

    public Dog(String name) {
//        super(name);
        System.out.println("Dog Name = " + name);
    }

    public static void main(String[] args) {
        Animal animal = new Animal();
        System.out.println("1-----------");
        Animal animal1 = new Animal("hahaha");
        System.out.println("2-----------");
        Dog dog = new Dog();
        System.out.println("3-----------");
        Dog dog1 = new Dog("HEIHEIHEI");
        System.out.println("4-----------");
    }
}
