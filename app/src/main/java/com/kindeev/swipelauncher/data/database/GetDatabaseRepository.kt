package com.kindeev.swipelauncher.data.database

import com.kindeev.swipelauncher.data.database.repositories.CircleMenuRepository
import com.kindeev.swipelauncher.data.database.repositories.DatabaseRepository
import com.kindeev.swipelauncher.data.database.repositories.SettingsRepository
import com.kindeev.swipelauncher.domain.interfaces.DataRepository

fun AppDataBase.getRepository(): DataRepository =
    DatabaseRepository(
        circleMenuRepository = CircleMenuRepository(circleMenuDao()),
        settingsRepository = SettingsRepository(settingsDao()),
    )