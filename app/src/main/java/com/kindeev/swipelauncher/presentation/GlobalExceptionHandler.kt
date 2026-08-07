package com.kindeev.swipelauncher.presentation

import android.content.Context
import android.content.Intent
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

class GlobalExceptionHandler private constructor(
    private val applicationContext: Context,
    private val defaultHandler: Thread.UncaughtExceptionHandler,
    private val activityToBeLaunched: Class<*>
): Thread.UncaughtExceptionHandler {
    override fun uncaughtException(p0: Thread, p1: Throwable) {
        try {

            val crashedIntent = Intent(applicationContext, activityToBeLaunched).also {
                it.putExtra(THROWABLE_KEY, printStackTraceToString(p1))
            }
            crashedIntent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            )
            crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            applicationContext.startActivity(crashedIntent)

            exitProcess(0)
        } catch (_: Exception) {
            defaultHandler.uncaughtException(p0, p1)
        }
    }

    private fun printStackTraceToString(throwable: Throwable): String {
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))
        return sw.toString()
    }

    companion object {
        const val THROWABLE_KEY = "throwable"

        fun initialize(
            applicationContext: Context,
            activityToBeLaunched: Class<*>
        ) {
            val handler = GlobalExceptionHandler(
                applicationContext,
                Thread.getDefaultUncaughtExceptionHandler() as Thread.UncaughtExceptionHandler,
                activityToBeLaunched
            )
            Thread.setDefaultUncaughtExceptionHandler(handler)
        }
    }
}