package com.example.helloworld

import com.example.helloworld.web.HttpUtil
import kotlinx.coroutines.*
import java.lang.Exception

fun main() {
    GlobalScope.launch {
        println("code run on coroutine scope")
    }
    CoroutineScope(Dispatchers.Default).launch {

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

        val result = async {
            6 + 6
        }.await()

        val res = withContext(Dispatchers.Default) {
            5 + 5
        }
        println(result)
    }
}

suspend fun printDot() = coroutineScope {
    launch {
        println(".")
        delay(1000)
    }
}

suspend fun getBaiduResponse() {
    try {
        val response = HttpUtil.request("https://www.baidu.com")
    } catch (e: Exception) {

    }
}

