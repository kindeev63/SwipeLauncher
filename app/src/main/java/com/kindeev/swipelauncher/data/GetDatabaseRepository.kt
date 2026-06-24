package com.kindeev.swipelauncher.data

import com.kindeev.swipelauncher.data.repositories.ApplicationDataRepository
import com.kindeev.swipelauncher.data.repositories.CircleMenuRepository
import com.kindeev.swipelauncher.data.repositories.DatabaseRepository
import com.kindeev.swipelauncher.data.repositories.SettingsRepository
import com.kindeev.swipelauncher.domain.interfaces.DataRepository

fun AppDataBase.getRepository(): DataRepository =
    DatabaseRepository(
        circleMenuRepository = CircleMenuRepository(circleMenuDao()),
        settingsRepository = SettingsRepository(settingsDao()),
        applicationDataRepository = ApplicationDataRepository(applicationDataDao())
    )