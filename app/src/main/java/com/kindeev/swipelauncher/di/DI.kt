package com.kindeev.swipelauncher.di

import android.content.Context
import com.kindeev.swipelauncher.data.dataBases.application_data.ApplicationDataDatabase
import com.kindeev.swipelauncher.data.dataBases.application_data.ApplicationDataDatabaseManager
import com.kindeev.swipelauncher.data.dataBases.circle_menu.CircleMenuDatabase
import com.kindeev.swipelauncher.data.dataBases.circle_menu.CircleMenuDatabaseManager
import com.kindeev.swipelauncher.data.dataBases.circle_menu_parameters.CircleMenuParametersDatabase
import com.kindeev.swipelauncher.data.dataBases.circle_menu_parameters.CircleMenuParametersDatabaseManager
import com.kindeev.swipelauncher.data.preferences.SettingsManager
import com.kindeev.swipelauncher.domain.database.ApplicationDataRepository
import com.kindeev.swipelauncher.domain.database.CircleMenuParametersRepository
import com.kindeev.swipelauncher.domain.database.CircleMenuRepository
import com.kindeev.swipelauncher.domain.database.SettingsRepository
import com.kindeev.swipelauncher.domain.useCases.newi.ApplicationDataDataState
import com.kindeev.swipelauncher.domain.useCases.newi.CircleMenuDataState

object DI {

    fun setup(context: Context) {
        setupDatabases(context)
        setupSettings(context)
    }

    private fun setupDatabases(context: Context) {
        val applicationDataDao = ApplicationDataDatabase.getDatabase(context).getDao()
        applicationDataRepository = ApplicationDataDatabaseManager(applicationDataDao)
        applicationDataDataState = ApplicationDataDataState(applicationDataRepository)

        val circleMenuDataDao = CircleMenuDatabase.getDatabase(context).getDao()
        circleMenuRepository = CircleMenuDatabaseManager(circleMenuDataDao)
        circleMenuDataState = CircleMenuDataState(circleMenuRepository)

        val circleMenuParametersDao = CircleMenuParametersDatabase.getDatabase(context).getDao()
        circleMenuParametersRepository = CircleMenuParametersDatabaseManager(circleMenuParametersDao)
    }

    private fun setupSettings(context: Context) {
        settingsRepository = SettingsManager(context)
    }

    private lateinit var applicationDataRepository: ApplicationDataRepository
    private lateinit var circleMenuRepository: CircleMenuRepository
    private lateinit var circleMenuParametersRepository: CircleMenuParametersRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var applicationDataDataState: ApplicationDataDataState
    private lateinit var circleMenuDataState: CircleMenuDataState

    object Dependencies {
        fun getApplicationDataRepository() = applicationDataRepository
        fun getCircleMenuRepository() = circleMenuRepository
        fun getCircleMenuParametersRepository() = circleMenuParametersRepository
        fun getSettingsRepository() = settingsRepository
        fun getApplicationDataDataState() = applicationDataDataState
        fun getCircleMenuDataState() = circleMenuDataState
    }
}