package com.kindeev.swipelauncher.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.kindeev.swipelauncher.data.DataObject
import com.kindeev.swipelauncher.data.DataObject.AppDataObject.isAppInstalled
import com.kindeev.swipelauncher.data.DataObject.CircleMenuDataObject.checkCircleMenus
import com.kindeev.swipelauncher.data.DataObject.ReceiverDataObject.receiverGetNewApplicationData
import com.kindeev.swipelauncher.data.DataObject.ReceiverDataObject.receiverSetAllApplicationData
import com.kindeev.swipelauncher.data.DataObject.SettingDataObject.clickableClockSettingValue
import com.kindeev.swipelauncher.data.DataObject.getAs
import com.kindeev.swipelauncher.data.settings.ApplicationSetting
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenApp
import com.kindeev.swipelauncher.presentation.MainApp


class AppsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val handler = Handler(Looper.getMainLooper())

        val myThread = Thread {
            // Ваш код, который должен выполняться в фоновом потоке
            val newApplicationData = receiverGetNewApplicationData(context)

            handler.post {
                // Обновление UI в основном потоке
                val mainAppViewModel = (context.applicationContext as MainApp).mainAppViewModel
                receiverSetAllApplicationData(newApplicationData)
                checkCircleMenus(mainAppViewModel, context)
                val clickableClockSetting = clickableClockSettingValue(
                    mainAppViewModel.allSettings.value ?: emptyList()
                )
                clickableClockSetting.circleMenuAction?.let { circleMenuAction ->
                    when (circleMenuAction.type) {
                        CircleMenuActionTypes.OpenApp -> {
                            val openApp = circleMenuAction.data.getAs(OpenApp::class.java)
                            if (!isAppInstalled(openApp.packageName, context)) {
                                DataObject.SettingDataObject.defaultSettings.find { it.setting == ApplicationSetting.ClickableClock }
                                    ?.let {
                                        mainAppViewModel.insertSetting(it)
                                    }
                            }
                        }

                        else -> {}
                    }
                }
            }
        }

        myThread.start()
    }
}