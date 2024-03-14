package com.kindeev.swipelauncher.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.kindeev.swipelauncher.domain.DataObject
import com.kindeev.swipelauncher.domain.DataObject.AppDataObject.getAllApplicationData
import com.kindeev.swipelauncher.domain.DataObject.AppDataObject.isAppInstalled
import com.kindeev.swipelauncher.domain.DataObject.AppDataObject.setAllApplicationData
import com.kindeev.swipelauncher.domain.DataObject.CircleMenuDataObject.checkCircleMenus
import com.kindeev.swipelauncher.domain.DataObject.SettingDataObject.clickableClockSettingValue
import com.kindeev.swipelauncher.domain.DataObject.getAs
import com.kindeev.swipelauncher.domain.entities.settings.ApplicationSetting
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionTypes.OpenApp
import com.kindeev.swipelauncher.presentation.MainApp


class AppsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Thread {
            val newApplicationData = context.getAllApplicationData()
            Handler(Looper.getMainLooper()).post {
                val mainAppViewModel = (context.applicationContext as MainApp).mainAppVM
                setAllApplicationData(newApplicationData)
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
        }.start()
    }
}