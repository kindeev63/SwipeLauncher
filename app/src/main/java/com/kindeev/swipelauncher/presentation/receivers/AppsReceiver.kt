package com.kindeev.swipelauncher.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.kindeev.swipelauncher.data.DataObject
import com.kindeev.swipelauncher.presentation.MainApp


class AppsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val handler = Handler(Looper.getMainLooper())

        val myThread = Thread {
            // Ваш код, который должен выполняться в фоновом потоке
            val newApplicationData = DataObject.receiverGetNewApplicationData(context)

            handler.post {
                // Обновление UI в основном потоке
                DataObject.receiverSetAllApplicationData(newApplicationData)
                DataObject.checkCircleMenus((context.applicationContext as MainApp).mainAppViewModel, context)
            }
        }

        myThread.start()
    }
}