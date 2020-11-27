package com.example.helloworld

object Singleton {
    var x : Int = 0
    fun goGoGo() {
        println("YES? $x")
    }

    fun setXXX(x : Int) {
        if (this.x != x) {
            this.x = x
        }
    }
}

fun main() {
    Singleton.goGoGo()
    Singleton.setXXX(5)
    Singleton.goGoGo()
    val list = listOf('1', "2", " 3")
    val map : Map<String, Int> = mapOf<String, Int>("hei" to 2, "pingguo" to 3)
    val maxLength = map.maxBy { it.key.length }

    val newMap = map.filter { it.key.length <= 3 }
    if (maxLength != null) {
        println(maxLength.value)
    }
    println(newMap)
}