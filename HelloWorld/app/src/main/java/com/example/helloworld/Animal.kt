package com.example.helloworld

open class Animall (val name :String){
    init {
        println("Animal$name")
    }

    fun eat() {
        print("EAT!")
    }
}

fun main() {
    val animal = Animall("HAHA")
    animal.eat()
    println(animal.name)
}