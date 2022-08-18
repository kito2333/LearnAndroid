package com.example.helloworld

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.lang.RuntimeException

open class Animall(val name: String) {
    init {
        println("Animal$name")
    }

    fun eat() {
        print("EAT!")
    }
}

fun max(vararg numbers: Int): Int {
    var max = Int.MIN_VALUE
    for (num in numbers) {
        max = kotlin.math.max(num, max)
    }
    return max
}

fun <T : Comparable<T>> max(vararg numbers: T): T {
    if (numbers.isEmpty()) throw RuntimeException("Params Invalid")
    var max = numbers[0]
    for (num in numbers) {
        if (num > max) {
            max = num
        }
    }
    return max
}

fun String.showToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

fun Int.showToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

fun View.showSnackBar(text: String, actionText: String? = null, duration: Int = Snackbar.LENGTH_SHORT, block: (() -> Unit)? = null) {
    val snackBar = Snackbar.make(this, text, duration)
    if (actionText != null && block != null) {
        snackBar.setAction(actionText) {
            block()
        }
    }
    snackBar.show()
}

fun main() {
    val animal = Animall("HAHA")
    animal.eat()
    println(animal.name)
}