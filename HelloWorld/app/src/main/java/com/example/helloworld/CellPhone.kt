package com.example.helloworld

data class CellPhone(val brand: String, val price: Double)

class Dogg(val legs: Int, name: String) : Animall(name) {
    constructor(name: String) : this(4, name) {
        println("first constructor")
    }

    constructor() : this(" haha ") {
        println("second constructor")
    }

    fun printName() {
        println(name)
    }
}

fun printInfo(id: Int = 1, time: Float, content: String = ".") {
    println(" id : $id, time : $time, content : $content")
}

fun main() {
    val dog = Dogg(3, "?")
    val dog1 = Dogg(3, "?")
    dog.printName()
    printInfo(time = 1.0f)
/*    val cellPhone1 = CellPhone("Sanmsung", 1299.9)
    val cellPhone2 = CellPhone("Sanmsung", 1299.9)
    println(cellPhone1.toString() + "\n" + cellPhone2.toString())
    println("cellPhone1 == cellPhone2 : " + (cellPhone1 == cellPhone2))*/
}