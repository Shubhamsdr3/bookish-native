package com.newaura.bookish.core.util

object AppLogger {
    private const val TAG = "Bookish"

    fun d(message: String) {
        println("[$TAG] DEBUG: $message")
    }

    fun e(message: String?, throwable: Throwable? = null) {
        println("[$TAG] ERROR: $message")
        throwable?.printStackTrace()
    }

    fun e(throwable: Throwable? = null) {
        println("[$TAG] ERROR: ${throwable?.message}")
        throwable?.printStackTrace()
    }

    fun e(message: String? = null) {
        println("[$TAG] ERROR: $message")
    }

    fun i(message: String) {
        println("[$TAG] INFO: $message")
    }

    fun w(message: String) {
        println("[$TAG] WARN: $message")
    }

    fun v(message: String) {
        println("[$TAG] VERBOSE: $message")
    }
}

