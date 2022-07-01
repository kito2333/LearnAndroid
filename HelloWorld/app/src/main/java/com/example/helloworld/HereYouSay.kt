package com.example.helloworld

import kotlinx.coroutines.*

fun main() {
    GlobalScope.launch {
        println("code run on coroutine scope")
    }
    runBlocking {
        launch {
            println("test 1")
            delay(1500)
            println("test 2")
        }

        launch {
            println("test 3")
            delay(1500)
            println("test 4")
        }
    }
}

suspend fun printDot() = coroutineScope {
    launch {
        println(".")
        delay(1000)
    }
}

