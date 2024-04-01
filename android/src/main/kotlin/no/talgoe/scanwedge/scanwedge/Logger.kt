package no.talgoe.scanwedge.scanwedge

import android.util.Log

interface Logger{
    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
    fun w(tag: String, message: String)
    fun e(tag: String, message: String)
}

class AndroidLogger : Logger {
    override fun d(tag: String, message: String) {
        Log.d(tag, message)
    }
    override fun i(tag: String, message: String) {
        Log.i(tag, message)
    }
    override fun w(tag: String, message: String) {
        Log.w(tag, message)
    }
    override fun e(tag: String, message: String) {
        Log.e(tag, message)
    }
}

class TestLogger : Logger {
    override fun d(tag: String, message: String) {
        println("\u001b[32m[DEBUG]\u001b[0m $tag: $message")
    }
    override fun i(tag: String, message: String) {
        println("\u001b[33m[INFO]\u001b[0m $tag: $message")
    }
    override fun w(tag: String, message: String) {
        println("\u001b[31m\u001b[1m[WARN]\u001b[0m $tag: $message")
    }
    override fun e(tag: String, message: String) {
        println("\u001b[41m\u001b[37m\u001b[1m[ERR]\u001b[0m $tag: $message")
    }
}