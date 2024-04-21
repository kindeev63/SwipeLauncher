package com.kindeev.swipelauncher.presentation

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import kotlin.system.exitProcess

class GlobalExceptionHandler private constructor(
    private val applicationContext: Context,
    private val defaultHandler: Thread.UncaughtExceptionHandler,
    private val activityToBeLaunched: Class<*>
): Thread.UncaughtExceptionHandler {
    override fun uncaughtException(p0: Thread, p1: Throwable) {
        try {

            val crashedIntent = Intent(applicationContext, activityToBeLaunched).also {
                it.putExtra(THROWABLE_KEY, Gson().toJson(p1))
            }
            crashedIntent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            )
            crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            applicationContext.startActivity(crashedIntent)

            exitProcess(0)
        } catch (e: Exception) {
            defaultHandler.uncaughtException(p0, p1)
        }
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